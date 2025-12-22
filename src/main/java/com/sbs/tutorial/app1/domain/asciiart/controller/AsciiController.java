package com.sbs.tutorial.app1.domain.asciiart.controller;

import com.sbs.tutorial.app1.domain.asciiart.entity.Ascii;
import com.sbs.tutorial.app1.domain.asciiart.form.Asciiform;
import com.sbs.tutorial.app1.domain.asciiart.service.AsciiService;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import exception.DataNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.zip.DataFormatException;

@RequestMapping("/ascii")
@RequiredArgsConstructor
@Controller
public class AsciiController {

    private final MemberService memberService;
    private final AsciiService asciiService;
//작품목록 비로그인 도 확인가능

    @GetMapping("/art")
    public String art() {
        return "ASCIIArt";
    }
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sort", defaultValue = "createDate") String sort,
                       @RequestParam(value = "dir", defaultValue = "desc") String dir) {

        Page<Ascii> paging = asciiService.getPublicList(page, kw, sort, dir);

        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        return "ascii_list";
    }
    //작품상세
    @GetMapping("/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         Principal principal) {

        Ascii ascii = asciiService.getAscii(id);
        //수정 삭제버튼 숨김처리
        boolean isOwner = principal != null && ascii.getOwner().getEmail().equals(principal.getName());
        //비공개 작품일경우 소유자만
        if (!ascii.isPublic()) {
            if (principal == null || !ascii.getOwner().getEmail().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "열람 권한이 없습니다.");
            }
        }
        model.addAttribute("ascii", ascii);
        model.addAttribute("isOwner", isOwner);//이렇게 하면 삭제 수정버튼을 권한에따라 보이게 또는 안보이게 할수 있음
        return "ascii_detail";
    }
    //작품작성 로그인 필요
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(Asciiform asciiForm, Principal principal) {
        if (principal == null || "anonymousUser".equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "로그인이 필요합니다.");
        }
        return "ascii_form";
    }
    //작품작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid Asciiform asciiForm,
                         BindingResult bindingResult,
                         Principal principal) {

        if (principal == null || "anonymousUser".equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "로그인이 필요합니다.");
        }

        if (bindingResult.hasErrors()) {
            return "ascii_form";
        }

        Member member = memberService.getMemberByEmail(principal.getName());

        asciiService.create(asciiForm.getSubject(), asciiForm.getContent(), asciiForm.isPublic(), member);

        return "redirect:/ascii/list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String asciiModify(Asciiform asciiform, @PathVariable("id") Integer id, Principal principal) {
        Ascii ascii = asciiService.getAscii(id);

        if (!ascii.getOwner().getEmail().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"수정 권한이 없습니다");
        }

        asciiform.setSubject(ascii.getSubject());
        asciiform.setContent(ascii.getContent());
        asciiform.setPublic(ascii.isPublic());

        return "ascii_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String asciiModify(@Valid Asciiform asciiform, BindingResult bindingResult,
                              Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "ascii_form";
        }

        Ascii ascii = asciiService.getAscii(id);
        if (!ascii.getOwner().getEmail().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        asciiService.modify( ascii, asciiform.getSubject(), asciiform.getContent(), asciiform.isPublic());
        return String.format("redirect:/ascii/detail/%d", id);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id,
                         Principal principal,
                         @RequestHeader(value = "Referer", required = false) String referer) {

        Ascii ascii = asciiService.getAscii(id);

        if (!ascii.getOwner().getEmail().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        asciiService.delete(ascii);

        //상세페이지에서 삭제한 경우= 삭제된 상세로 돌아가면 404 -> 목록으로 보냄
        if (referer != null && referer.contains("/ascii/detail/")) {
            return "redirect:/ascii/list";
        }
        //그외= 이전 페이지로 돌아가기 마이페이지 등
        if (referer != null) {
            try {
                URI uri = URI.create(referer);
                String path = uri.getPath();
                String query = uri.getQuery();

                //우리 서비스 경로만 허용
                if (path != null && path.startsWith("/ascii")) {
                    return "redirect:" + path + (query != null ? "?" + query : "");
                }
            } catch (Exception ignored) {}
        }

        return "redirect:/ascii/my";
    }
    //삭제된 글 뒤로가기 시 404 페이지대신 뒤로가서 보여주기
    @ExceptionHandler(DataNotFoundException.class)
    public String handleAsciiNotFound(DataNotFoundException e) {
        return redirectAlert("이미 삭제되었거나 존재하지 않는 작품입니다.", "/ascii/list");
    }
    private String redirectAlert (String msg, String next) {
        String m = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        String n = URLEncoder.encode(next, StandardCharsets.UTF_8);
        return "redirect:/alert?msg=" + m + "&next=" + n;
    }

   // @PreAuthorize("isAuthenticated()") 이부분 제거하면 로그인이 필요하면
    @GetMapping("/my")
    public String myPage(Model model,
                         Principal principal,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "kw", defaultValue = "") String kw,
                         @RequestParam(value = "sort", defaultValue = "createDate") String sort,
                         @RequestParam(value = "dir", defaultValue = "desc") String dir) {

        Member me = memberService.getMemberByEmail(principal.getName());
        Page<Ascii> paging = asciiService.getMyPage(me, page, kw, sort, dir);

        model.addAttribute("owner", me);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("isMyPage", true);

        return "ascii_mypage";
    }
    @GetMapping("/user/{memberId}")
    public String userPage(Model model,
                           @PathVariable("memberId") Long memberId,
                           Principal principal,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "kw", defaultValue = "") String kw,
                           @RequestParam(value = "sort", defaultValue = "createDate") String sort,
                           @RequestParam(value = "dir", defaultValue = "desc") String dir) {

        Member owner = memberService.getMemberById(memberId);

        boolean isOwner = principal != null && owner.getEmail().equals(principal.getName());

        Page<Ascii> paging = isOwner
                ? asciiService.getMyPage(owner, page, kw, sort, dir)          // 내 글 전체(공개+비공개)
                : asciiService.getUserPublicPage(owner, page, kw, sort, dir); // 남의 글(공개만)

        model.addAttribute("owner", owner);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("isMyPage", isOwner);

        return "ascii_mypage";
    }
}
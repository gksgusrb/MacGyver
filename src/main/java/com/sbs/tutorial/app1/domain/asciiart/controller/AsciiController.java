package com.sbs.tutorial.app1.domain.asciiart.controller;

import com.sbs.tutorial.app1.domain.asciiart.entity.Ascii;
import com.sbs.tutorial.app1.domain.asciiart.form.Asciiform;
import com.sbs.tutorial.app1.domain.asciiart.service.AsciiService;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RequestMapping("/ascii")
@RequiredArgsConstructor
@Controller
public class AsciiController {

    private final MemberService memberService;
    private final AsciiService asciiService;
//작품목록 비로그인 도 확인가능
    @GetMapping("/list")
    public String list(Model model) {
        List<Ascii> asciiList = asciiService.getAllPublicList();
        model.addAttribute("asciiList", asciiList);
        return "ascii_list";
    }
    //작품상세
    @GetMapping("/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         Principal principal) {

        Ascii ascii = asciiService.getAscii(id);
        //비공개 작품일경우 소유자만
         if (!ascii.isPublic()) {
            if (principal == null || !ascii.getOwner().getEmail().equals(principal.getName())) {
                 throw new ResponseStatusException(HttpStatus.FORBIDDEN, "열람 권한이 없습니다.");
             }
         }
        model.addAttribute("ascii", ascii);
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
    @GetMapping("/delete/{id}")
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
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public String myPage(Model model, Principal principal) {

        Member me = memberService.getMemberByEmail(principal.getName());

        List<Ascii> asciiList = asciiService.getMyList(me);

        model.addAttribute("owner", me);
        model.addAttribute("asciiList", asciiList);
        model.addAttribute("isMyPage", true); // 템플릿에서 사용

        return "ascii_mypage";
    }
    @GetMapping("/user/{memberId}")
    public String userPage(Model model,
                           @PathVariable("memberId") Long memberId,
                           Principal principal) {

        Member owner = memberService.getMemberById(memberId);

        boolean isOwner = false;
        if (principal != null) {
            isOwner = owner.getEmail().equals(principal.getName());
        }

        List<Ascii> asciiList;
        if (isOwner) {
            // 내가 나 자신의 페이지를 보는 경우 → 전체
            asciiList = asciiService.getMyList(owner);
        } else {
            // 다른 사람이 보는 경우 → 공개 작품만
            asciiList = asciiService.getPublicListByOwner(owner);
        }

        model.addAttribute("owner", owner);
        model.addAttribute("asciiList", asciiList);
        model.addAttribute("isMyPage", isOwner);

        return "ascii_mypage";
    }
}

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

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
    public String create(Asciiform asciiForm) {
        return "ascii_form";
    }
    //작품작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid Asciiform asciiForm,
                         BindingResult bindingResult,
                         Principal principal) {

        if (bindingResult.hasErrors()) {
            return "ascii_form";
        }
        Member member = memberService.getMemberByEmail(principal.getName());

        asciiService.create(
                asciiForm.getSubject(),
                asciiForm.getContent(),
                asciiForm.isPublic(),
                member
        );

        return "redirect:/ascii/list";
    }
}

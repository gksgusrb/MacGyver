package com.sbs.tutorial.app1.authcontroller;

import com.sbs.tutorial.app1.authservice.AuthService;
import com.sbs.tutorial.app1.emailservice.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailService emailService;
    private final AuthService authService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        emailService.sendVerificationCode(email);
        return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAndRegister(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String code) {

        authService.verifyCodeAndRegister(email, username, code);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}

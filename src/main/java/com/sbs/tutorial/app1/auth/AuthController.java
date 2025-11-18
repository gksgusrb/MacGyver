package com.sbs.tutorial.app1.auth;

import com.sbs.tutorial.app1.email.service.EmailService;
import com.sbs.tutorial.app1.user.User;
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
    }// ^^회원가입 컨트롤러^^
    @PostMapping("/login/send-code")
    public ResponseEntity<String> SendloginCode(@RequestParam String email) {
        emailService.sendloginCode(email);
        return ResponseEntity.ok(" 로그인 인증번호가 이메일로 발송되었습니다");
    }
    @PostMapping("/login/verify")
    public ResponseEntity<String> verifylogin(
            @RequestParam String email,
            @RequestParam String code) {

        User user = authService.logincode(email, code);
        return ResponseEntity.ok("환영합니다.");
    }//^^ 로그인 컨트롤러^^

}

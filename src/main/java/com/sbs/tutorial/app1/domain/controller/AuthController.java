package com.sbs.tutorial.app1.domain.controller;

import com.sbs.tutorial.app1.domain.service.AuthService;
import com.sbs.tutorial.app1.domain.service.EmailService;
import com.sbs.tutorial.app1.domain.user.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailService emailService;
    private final AuthService authService;

    @GetMapping("/csrf-token")
    public Map<String, Object> csrfToken(CsrfToken token) {
        return Map.of(
                "headerName", token.getHeaderName(),
                "token", token.getToken()
        );
    }

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
    public ResponseEntity<String> SendLoginCode(@RequestParam String email) {
        emailService.sendloginCode(email);
        return ResponseEntity.ok(" 로그인 인증번호가 이메일로 발송되었습니다");
    }
    @PostMapping("/login/verify")
    public ResponseEntity<String> verifyLogin(
            @RequestParam String email,
            @RequestParam String code) {

        Member member = authService.logincode(email, code);
        return ResponseEntity.ok("환영합니다.");
    }//^^ 로그인 컨트롤러^^
// 로그인 유지 확인용 긁어옴 나중에는 지울듯
@GetMapping("/me")
public ResponseEntity<?> me() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
        return ResponseEntity.status(401).body("로그인되지 않았습니다.");
    }

    Object principal = auth.getPrincipal();
// 버그수정 그냥둘다 포함시켜봄
    // UserDetails 타입일 때
    if (principal instanceof UserDetails userDetails) {
        return ResponseEntity.ok("현재 로그인한 사용자: " + userDetails.getUsername());
    }

    // User 엔티티 타입일 때
    if (principal instanceof Member member) {
        return ResponseEntity.ok("현재 로그인한 사용자: " + member.getEmail());
    }

    return ResponseEntity.status(401).body("로그인 정보가 올바르지 않습니다.");
}
    // 로그아웃 기능
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        // 세션 초기화
        request.getSession().invalidate();

        // 시큐리티 인증 초기화
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃 완료");
    }
}

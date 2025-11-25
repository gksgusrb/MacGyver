package com.sbs.tutorial.app1.domain.member.controller;

import com.sbs.tutorial.app1.domain.member.service.MemberService;
import com.sbs.tutorial.app1.domain.email.service.EmailService;
import com.sbs.tutorial.app1.domain.member.entity.Member;
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
public class MemberController {

    private final EmailService emailService;
    private final MemberService memberService;

    //로그인 회원가입 버그 났을때 토크이 재대로 동작하는 지 확인하기 위해 사용
    @GetMapping("/csrf-token")
    public Map<String, Object> csrfToken(CsrfToken token) {
        return Map.of(
                "headerName", token.getHeaderName(),
                "token", token.getToken()
        );
    }

// html이나 post에 /api/auth/send-code 에 요청이 들어오면 String email 로 이메일을 가져와 실행
    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(String email) {
        // 이메일 서비스에 있는 public void sendVerificationCode(String email) 매서드 호출 그곳에 변수를 넘김
        emailService.sendVerificationCode(email);
        return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다");
    }
//api/auth/send-code/verify 요청 즉 인증번호를 완벽히 적고 회원가입 버튼을 눌렀을때 작동함
    @PostMapping("/verify")
    public ResponseEntity<String> verifyAndRegister(String email, String username, String code) { // 요청 받은 이메일 이름 압력받은 난수를 받아서 저장함
        memberService.verifyCodeAndRegister(email, username, code);//저장된 정보를 authService에 있는 verifyCodeAndRegister 에 보냄 예외 발생시 서비스에서 컨트러로 반환됨
        return ResponseEntity.ok("회원가입이 완료되었습니다."); // 모두 완료되어 끝이나면 리턴함
    }// ^^회원가입 컨트롤러^^

    @PostMapping("/login/send-code")
    public ResponseEntity<String> SendLoginCode(String email) {
        emailService.sendloginCode(email);
        return ResponseEntity.ok(" 로그인 인증번호가 이메일로 발송되었습니다");
    }
    @PostMapping("/login/verify")
    public ResponseEntity<String> verifyLogin(String email, String code) {

        Member member = memberService.logincode(email, code);
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
}

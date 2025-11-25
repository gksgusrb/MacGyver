package com.sbs.tutorial.app1.domain.service;

import com.sbs.tutorial.app1.domain.user.Member;
import com.sbs.tutorial.app1.domain.user.MemberRepository;
import com.sbs.tutorial.app1.domain.user.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;
    private final Map<String, String> fakeRedisStorage; //로컬

    public void verifyCodeAndRegister(String email, String username, String inputCode) {
        String cleanEmail = email.trim();// 코드 사용 1번만했음 그래서 전부 적용함

       // System.out.println(" 현재 fakeRedisStorage = " + fakeRedisStorage);
      //  System.out.println(" map.get() 결과 = " + fakeRedisStorage.get("verify:" + cleanEmail));

        String savedCode = null;
//savedCode 를 널로 설정하고 if문으로 가져옴
        if (fakeRedisStorage != null) {
            savedCode = fakeRedisStorage.get("verify:" + cleanEmail);
        } else {
            // prod: Redis에서 꺼냄
            savedCode = redisTemplate.opsForValue().get("verify:" + cleanEmail);
        }

        if (savedCode == null) {
            throw new RuntimeException("인증번호가 만료되었거나 존재하지 않습니다.");
        }
        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        if (memberRepository.existsByEmail(cleanEmail)) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        if (memberRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(cleanEmail)
                .username(username)
                .verified(true)
                .role(MemberRole.USER)
                .build();
        memberRepository.save(member);

        if (fakeRedisStorage != null) {
            fakeRedisStorage.remove("verify:" + cleanEmail);
        } else if (redisTemplate != null) {
            redisTemplate.delete("verify:" + cleanEmail);
        }
    }
    public Member logincode(String email, String inputCode) {
        String cleanEmail = email.trim();
        Member member = memberRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new RuntimeException("없는 이메일입니다"));

        String savedCode;
        if (fakeRedisStorage != null) {
            savedCode = fakeRedisStorage.get("login:" + cleanEmail);
        } else {
            // prod: Redis에서 꺼냄
            savedCode = redisTemplate.opsForValue().get("login:" + cleanEmail);
        }

        if (savedCode == null) {
            throw new RuntimeException("인증번호가 만료되었거나 존재하지 않습니다.");
        }
        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(member.getRole().getValue()));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        new org.springframework.security.core.userdetails.User(
                                member.getEmail(),
                                "",
                                authorities
                        ),
                        null,
                        authorities
                );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        if (fakeRedisStorage != null) {
            fakeRedisStorage.remove("login:" + cleanEmail);
        } else if (redisTemplate != null) {
            redisTemplate.delete("login:" + cleanEmail);
        }
        return member;
    }

}
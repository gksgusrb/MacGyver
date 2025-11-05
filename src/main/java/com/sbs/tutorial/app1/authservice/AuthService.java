package com.sbs.tutorial.app1.authservice;

import com.sbs.tutorial.app1.user.User;
import com.sbs.tutorial.app1.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public void verifyCodeAndRegister(String email, String username, String inputCode) {
        String savedCode = redisTemplate.opsForValue().get("verify:" + email);

        if (savedCode == null) {
            throw new RuntimeException("인증번호가 만료되었거나 존재하지 않습니다.");
        }
        if (!savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(email)
                .username(username)
                .verified(true)
                .build();

        userRepository.save(user);

        redisTemplate.delete("verify:" + email);

    }
}
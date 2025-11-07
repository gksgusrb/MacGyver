package com.sbs.tutorial.app1.auth.authservice;

import com.sbs.tutorial.app1.user.User;
import com.sbs.tutorial.app1.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final Map<String, String> fakeRedisStorage; //로컬

    public void verifyCodeAndRegister(String email, String username, String inputCode) {

        String savedCode = null;
//savedCode 를 널로 설정하고 if문으로 가져옴
        if (fakeRedisStorage != null) {
            String cleanEmail = email.trim();
           savedCode = fakeRedisStorage.get("verify:" + cleanEmail);
        } else {
           // prod: Redis에서 꺼냄
          savedCode = redisTemplate.opsForValue().get("verify:" + email);
        }

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

        if (fakeRedisStorage != null) {
            fakeRedisStorage.remove("verify:" + email);
        } else if (redisTemplate != null) {
            redisTemplate.delete("verify:" + email);
        }


    }
}
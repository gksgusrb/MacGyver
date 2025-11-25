package com.sbs.tutorial.app1.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate; // prod 에서 사용
    private final Map<String, String> fakeRedisStorage; //dev에서 사용

    //아더 컨트롤에서 받은 이메일로 인증번호 생성
    public void sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999)); // 난수 생성
        //dev면 fake Redis사용 난수 생성후 가짜와 진짜를 구별함 근대 나는 dev로 설정해두었기 떄문 계속 가짜 사용중
        if (fakeRedisStorage != null) {
            fakeRedisStorage.put("verify:" + email, code);
        } else {
    // 아니면 Redis 사용
            redisTemplate.opsForValue().set("verify:" + email, code, 5, TimeUnit.MINUTES);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[MacGyver]이메일 인증번호");
        message.setText("인증번호는 " + code + " 입니다. (5분 유효)");

        mailSender.send(message);
//확인용
        //String cleanEmail = email.trim();
      //  if (fakeRedisStorage != null) {
      //      fakeRedisStorage.put("verify:" +  cleanEmail, code);
    //        System.out.println("저장됨 → " + fakeRedisStorage);
    //    } else {
     //       redisTemplate.opsForValue().set("verify:" + email, code, 5, TimeUnit.MINUTES);
    //    }
    }
    public void sendloginCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        //dev면 fake 사용
        if (fakeRedisStorage != null) {
            fakeRedisStorage.put("login:" + email, code);
        } else {
            // 아니면 Redis 사용
            redisTemplate.opsForValue().set("login:" + email, code, 5, TimeUnit.MINUTES);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[MacGyver]이메일 인증번호");
        message.setText("로그인 인증번호는 " + code + " 입니다. (5분 유효)");

        mailSender.send(message);
    }
}

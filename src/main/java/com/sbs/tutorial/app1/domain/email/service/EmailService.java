package com.sbs.tutorial.app1.domain.email.service;

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
    private final Map<String, String> fakeRedisStorage; //fake 에서 Bean 으로 저장된 클레스를  타입과 이름을 호출해 불러옴

    //아더 컨트롤에서 받은 이메일로 인증번호 생성
    public void sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999)); // 난수 생성
        //dev면 fake Redis사용 난수 생성후 가짜와 진짜를 구별함 근대 나는 dev로 설정해두었기 떄문 계속 가짜 사용중
        if (fakeRedisStorage != null) {
            fakeRedisStorage.put("verify:" + email, code);// 입력받은 이메일과 만든 난수를 akeRedisStorage 에 있는verify 에저장
        } else {
         // 아니면 Redis 사용
            redisTemplate.opsForValue().set("verify:" + email, code, 5, TimeUnit.MINUTES);
        }
//SimpleMailMessage 스프링에서 제공하는 메일 보내는 기능
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); //수신자 설정
        message.setSubject("[MacGyver]이메일 인증번호"); //제목
        message.setText("인증번호는 " + code + " 입니다. (5분 유효)");// 내용 과 코드를 집어 넣음

        mailSender.send(message);// 변수명 message 를 읽어 정보를 SMTP 서버에 연결해 진짜로 메세지를 보냄

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

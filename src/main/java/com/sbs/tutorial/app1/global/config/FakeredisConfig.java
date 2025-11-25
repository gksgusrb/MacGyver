package com.sbs.tutorial.app1.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Profile("dev") //이 설정으로 이 파일은 설정이 dev 일떄만 활성화됨
public class FakeredisConfig {
@Bean //이 아래쪽 클레스를 스켄해서 저장함 다른 파일에서 불러들일때 사용함
// private final Map<String, String> fakeRedisStorage; 으로 타입 map 이름 fakeRed 으로 저장

    public Map<String, String> fakeRedisStorage() {
    return new ConcurrentHashMap<>(); //이메일서비스 에서 받은 정보(이메일,난수)를  ConcurrentHashMap<>() 에 저장
     }
}
//회원 가입일 경우는 vaerify+이메일 라는 키에 인증번호라는 값을 저장한다
//로그인일 경우는 login+이메일 라는 키에 인증번호라는 값을 저장한다

package com.sbs.tutorial.app1.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Profile("dev") //이 설정으로 이 파일은 설정이 dev 일떄만 활성화됨
public class FakeredisConfig {
@Bean
    public Map<String, String> fakeRedisStorage() {
    return new ConcurrentHashMap<>();
     }
}

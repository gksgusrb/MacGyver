package com.sbs.tutorial.app1.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Profile("dev")
public class FakeredisConfig {
@Bean
    public Map<String, String> fakeRedisStorage() {
    return new ConcurrentHashMap<>();
     }
}

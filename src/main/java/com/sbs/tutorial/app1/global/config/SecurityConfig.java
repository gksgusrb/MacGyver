package com.sbs.tutorial.app1.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers( //이 곳에 적힌 url은 검사 제외
                                new AntPathRequestMatcher ("/api/auth/login/**"),
                                new AntPathRequestMatcher ("/api/auth/send-code"),
                                new AntPathRequestMatcher  ("/api/auth/verify"),
                                new AntPathRequestMatcher("/api/auth/logout")

                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( //로그인없이 접근 가능한 리소스들
                                "/",
                                "/index.html",
                                "/login",
                                "/newbody",
                                "/asciiart",
                                "/ascii",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/ascii/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()

                )


                // 일반적 로그인 기능이 작동하지않아 정보또한 자동 저장하지않아 직접 설정함
                .securityContext(security ->
                        security.requireExplicitSave(false)) //SecurityContext가 바뀌면 알아서 세션에 저장해라 (자동 저장)
                // 비밀번호 없이 로그인 하는 방식에서 버그를 일으켜서 추가함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )// 세션을 항상 한개는 만들어 두게 만들어 정보를 저장해 로그인 유지를 하게 만든다
                // 위에서 세션에 항상 저장하게 하고 세션하나는 무조건 유지하게 함으로 로그인 유지    커스텀 로그인의 영향으로 일반적 로그인 기능을 꺼버려 내가 직접 설정해 줘야했다
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())// 두옵션전부 커스텀 로그인으로 꺼버림
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") //로그아웃 이 해당 url로 온다면 싱행
                        .logoutSuccessUrl("/")//수행후 메인페이지로 이동
                        .invalidateHttpSession(true) //세션 제거
                        .clearAuthentication(true) //Authentication 도 제거
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN") //쿠키 제거
                );
        return http.build();
    }
}

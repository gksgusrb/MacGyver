package com.sbs.tutorial.app1.domain.asciiart.service;

import com.sbs.tutorial.app1.domain.asciiart.entity.Ascii;
import com.sbs.tutorial.app1.domain.asciiart.repository.AsciiRepository;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Profile({"dev", "test"})
@Component
@RequiredArgsConstructor
public class AsciiSeed implements CommandLineRunner {

    private final AsciiRepository asciiRepository;
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) {
        String email = "hankhme6079@gmail.com";

        Member owner = memberRepository.findByEmail(email).orElse(null);
        if (owner == null) return;

        if (asciiRepository.count() > 0) return; // 글이 1개라도있으면 테스트 데이터를 생성안함

        for (int i = 1; i <= 30; i++) {
            Ascii a = Ascii.builder()
                    .owner(owner)
                    .subject("테스트 작품 " + i)
                    .content("TEST ASCII " + i + "\n" +
                            " /\\_/\\\n" +
                            "( o.o )\n" +
                            " > ^ <\n")
                    .isPublic(i % 3 != 0)
                    .createDate(LocalDateTime.now().minusDays(30 - i))
                    .build();

            asciiRepository.save(a);
        }
    }
}
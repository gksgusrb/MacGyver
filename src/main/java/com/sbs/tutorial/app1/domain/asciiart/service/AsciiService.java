package com.sbs.tutorial.app1.domain.asciiart.service;

import com.sbs.tutorial.app1.domain.asciiart.entity.Ascii;
import com.sbs.tutorial.app1.domain.asciiart.repository.AsciiRepository;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class AsciiService {
    private final AsciiRepository asciiRepository;

    public Ascii getAscii(Integer id) {
        return asciiRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("ascii not found"));
    }

    public Ascii create(String subject, String content, boolean isPublic, Member owner) {
        Ascii ascii = Ascii.builder()
                .owner(owner)
                .subject(subject)
                .content(content)
                .isPublic(isPublic)
                .build();

        return asciiRepository.save(ascii);
    }
    public List<Ascii> getMyList(Member owner) {
        return asciiRepository.findByOwnerOrderByCreateDateDesc(owner);
    }

    public List<Ascii> getAllPublicList() {
        return asciiRepository.findByIsPublicTrueOrderByCreateDateDesc();
    }



}

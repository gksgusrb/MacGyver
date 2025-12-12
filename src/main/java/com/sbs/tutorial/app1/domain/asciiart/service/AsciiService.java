package com.sbs.tutorial.app1.domain.asciiart.service;

import com.sbs.tutorial.app1.domain.asciiart.entity.Ascii;
import com.sbs.tutorial.app1.domain.asciiart.repository.AsciiRepository;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class AsciiService {
    private final AsciiRepository asciiRepository;
// 상세페이지 로드
    public Ascii getAscii(Integer id) {
        return asciiRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("ascii not found"));
    }
//글 작성
    public Ascii create(String subject, String content, boolean isPublic, Member owner) {
        Ascii ascii = Ascii.builder()
                .owner(owner)
                .subject(subject)
                .content(content)
                .isPublic(isPublic)
                .build();

        return asciiRepository.save(ascii);
    }
    //개인 페이지 작품 공개 비공개 작품
    public List<Ascii> getMyList(Member owner) {
        return asciiRepository.findByOwnerOrderByCreateDateDesc(owner);
    }
// 다른 유자가 다른사람의 개인페이지를 보았을때 작품목록
    public List<Ascii> getPublicListByOwner(Member owner) {
        return asciiRepository.findByOwnerAndIsPublicTrueOrderByCreateDateDesc(owner);

    }

// 일반 유저들이 사용하는 공개작품모음
    public List<Ascii> getAllPublicList() {
        return asciiRepository.findByIsPublicTrueOrderByCreateDateDesc();
    }

//글 수정
    public void modify(Ascii ascii, String subject, String content, boolean isPublic) {
        ascii.setSubject(subject);
        ascii.setContent(content);
        ascii.setPublic(isPublic);//글 공개 비공개 여부
        ascii.setModifyDate(LocalDateTime.now());
        asciiRepository.save(ascii);
    }
//글삭제

    public void  delete(Ascii ascii) {
        asciiRepository.delete(ascii);
    }

}

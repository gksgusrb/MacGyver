package com.sbs.tutorial.app1.domain.asciiart.entity;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor @AllArgsConstructor @Builder
public class Ascii {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //만든이를 지정해 그사람의 공개 개인페이지를 볼수있고 장품목록을 환인 가능하게 만듬
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    //제목
    @Column(length = 200)
    private  String subject;

    //아스키 아트 텍스트 넣을 부분 나중에 여기게 txt 파일을 넣어 글은 못쓰고 오로지 작품만 담을수있게
    @Column(columnDefinition = "TEXT")
    private String content;

    //작품 공개 비공개 설정
    @Column(nullable = false)
    private boolean isPublic;

    //생성 일자와 수정일자
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "ascii", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<AsciiComment> commentList = new ArrayList<>();
/*
    @ManyToMany
    Set<SiteUser> voter = new HashSet<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Answer> answerList = new ArrayList<>();

    public void addAnswer(Answer answer) {
        answer.setQuestion(this);
        answerList.add(answer);
    }

    public void addVoter(SiteUser siteUser) {
        voter.add(siteUser);
    }
*/
}


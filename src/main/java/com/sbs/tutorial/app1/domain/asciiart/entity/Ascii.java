package com.sbs.tutorial.app1.domain.asciiart.entity;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Builder.Default // Lombok @Builder 쓰는 엔티티는 필드 초기값이 무시될 수 있음 그래서 빌더로 만들 때도 기본값(HashSet)을 유지하라 라는뜻 null 방지용으로 넣음
    @ManyToMany
    @JoinTable( // N : M 관계는 db에서 중간 테이블이 꼭필요해서 사요함
            name = "ascii_like",
            joinColumns = @JoinColumn(name = "ascii_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @ToString.Exclude //Ascii가 Member를 갖고, Member가 또 Ascii를 갖고 이런 식으로 양방향 연관관계가 걸리면 toString() 출력 시 무한 순환(스택오버플로우) 그래서 제외시킴
    private Set<Member> likedMembers = new HashSet<>();
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
package com.sbs.tutorial.app1.domain.asciiart.repository;

import com.sbs.tutorial.app1.domain.asciiart.entity.Ascii;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AsciiRepository extends JpaRepository<Ascii, Integer>, JpaSpecificationExecutor<Ascii> {
    // 내 작품 보기 공개 비공개 전부
    List<Ascii> findByOwnerOrderByCreateDateDesc(Member owner);

    // 다른사람이 나의 공개페이지 보기 공개만
    List<Ascii> findByOwnerAndIsPublicTrueOrderByCreateDateDesc(Member owner);

    // 다른사람이 모든사람의 작품을 보기 공개만
    List<Ascii> findByIsPublicTrueOrderByCreateDateDesc();
}
//findBy + 필드와 조건 + OrderBy + 정렬 기준 필드 +Asc Desc 를 맞춰 쓰다보니 길어지는문제
// 나중에 강사님꺼 처럼 findBySubjectAndContent 로 검색 기능도 추가할 예정
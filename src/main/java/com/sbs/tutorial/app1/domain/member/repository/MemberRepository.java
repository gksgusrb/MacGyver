package com.sbs.tutorial.app1.domain.member.repository;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
//이부분을 이렇게 씀으로 자동으로 스프링 데이터 JPA가 제공하는 기본 CRUD 기능을 불러옴 상속시 자동으로 매소드들을 생성함
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email); //맴버를 추출
    boolean existsByEmail(String email); //이메일 을 가진 맴버가 있는지?
    boolean existsByUsername(String username);// 같은 이름을 사용하는 유저가 존제하는지?
}
package com.sbs.tutorial.app1.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder //편리하다함 근대 @AllArgConstructor 없으면 버그남
@Table(name = "users")
public class Member { //가입된 유저 정보를 저장하고 꺼내옴
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    private boolean verified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.USER;
}

package com.sbs.tutorial.app1.domain.member.entity;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    MemberRole(String value) {
        this.value = value;
    }
    private String value;
}//만들어놓고 권한 부여를 하지않아 모든 유저에게 권한이 부여되지않아 보는것은 가능하지만
//로그인 회원가입등 아무것도 안되는 버그 발생함 권한 설정을 지정하지 않으면 비회원이라도 영향을 받는듯 하다

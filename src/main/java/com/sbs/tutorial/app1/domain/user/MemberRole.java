package com.sbs.tutorial.app1.domain.user;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_YOU");

    MemberRole(String value) {
        this.value = value;
    }
    private String value;
}

package com.sbs.tutorial.app1.domain.asciiart.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Asciiform {
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=50, message="제목은 50자 이내로 입력해주세요.")
    private String subject;

    @NotEmpty(message="내용은 필수항목입니다.")
    @Size(max=200000, message="내용은 200000자 이내로 입력해주세요.")
    private String content;

    private boolean isPublic;// 공개여부 확인
}

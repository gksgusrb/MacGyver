package com.sbs.tutorial.app1.domain.asciiart.entity;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsciiComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 작품의 댓글인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ascii_id", nullable = false)
    @ToString.Exclude
    private Ascii ascii;

    // 댓글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

}

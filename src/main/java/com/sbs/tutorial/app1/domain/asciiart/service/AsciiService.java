package com.sbs.tutorial.app1.domain.asciiart.service;

import com.sbs.tutorial.app1.domain.asciiart.entity.Ascii;
import com.sbs.tutorial.app1.domain.asciiart.repository.AsciiRepository;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import exception.DataNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class AsciiService {

    private final AsciiRepository asciiRepository;

    // ===== 검색 조건 (강사님 방식) =====
    private Specification<Ascii> search(String kw) {
        return (Root<Ascii> a, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);

            Join<Ascii, Member> m = a.join("owner", JoinType.LEFT);

            String like = "%" + kw + "%";
            return cb.or(
                    cb.like(a.get("subject"), like),
                    cb.like(a.get("content"), like),
                    cb.like(m.get("username"), like)
                    //cb.like(m.get("email"), like) -> 이거떄문에 메일 번호도 참조해서 제가함
            );
        };
    }

    private Specification<Ascii> isPublicTrue() {
        return (root, query, cb) -> cb.isTrue(root.get("isPublic"));
    }

    private Specification<Ascii> ownerIs(Member owner) {
        return (root, query, cb) -> cb.equal(root.get("owner"), owner);
    }

    // ===== 페이징/검색/정렬 =====
    public Page<Ascii> getPublicList(int page, String kw, String sort, String dir) {
        Pageable pageable = buildPageable(page, sort, dir);

        Specification<Ascii> spec = Specification.where(isPublicTrue());
        if (kw != null && !kw.trim().isEmpty()) spec = spec.and(search(kw.trim()));

        return asciiRepository.findAll(spec, pageable);
    }

    public Page<Ascii> getMyPage(Member me, int page, String kw, String sort, String dir) {
        Pageable pageable = buildPageable(page, sort, dir);

        Specification<Ascii> spec = Specification.where(ownerIs(me));
        if (kw != null && !kw.trim().isEmpty()) spec = spec.and(search(kw.trim()));

        return asciiRepository.findAll(spec, pageable);
    }

    public Page<Ascii> getUserPublicPage(Member owner, int page, String kw, String sort, String dir) {
        Pageable pageable = buildPageable(page, sort, dir);

        Specification<Ascii> spec = Specification.where(ownerIs(owner)).and(isPublicTrue());
        if (kw != null && !kw.trim().isEmpty()) spec = spec.and(search(kw.trim()));

        return asciiRepository.findAll(spec, pageable);
    }

    private Pageable buildPageable(int page, String sort, String dir) {
        String sortField = switch (sort == null ? "" : sort) {
            case "subject" -> "subject";
            case "createDate" -> "createDate";
            default -> "createDate";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, 10, Sort.by(new Sort.Order(direction, sortField)));
    }

    // ===== 기존 CRUD =====
    public Ascii getAscii(Integer id) {
        return asciiRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("ascii not found"));
    }

    public Ascii create(String subject, String content, boolean isPublic, Member owner) {
        Ascii ascii = Ascii.builder()
                .owner(owner)
                .subject(subject)
                .content(content)
                .isPublic(isPublic)
                .createDate(LocalDateTime.now())
                .build();

        return asciiRepository.save(ascii);
    }

    public void modify(Ascii ascii, String subject, String content, boolean isPublic) {
        ascii.setSubject(subject);
        ascii.setContent(content);
        ascii.setPublic(isPublic);
        ascii.setModifyDate(LocalDateTime.now());
        asciiRepository.save(ascii);
    }

    public void delete(Ascii ascii) {
        asciiRepository.delete(ascii);
    }
}

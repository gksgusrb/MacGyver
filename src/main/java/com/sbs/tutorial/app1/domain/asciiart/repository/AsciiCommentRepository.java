package com.sbs.tutorial.app1.domain.asciiart.repository;
import com.sbs.tutorial.app1.domain.asciiart.entity.Ascii;
import com.sbs.tutorial.app1.domain.asciiart.entity.AsciiComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsciiCommentRepository extends JpaRepository<AsciiComment, Long> {
    List<AsciiComment> findByAsciiOrderByCreateDateAsc(Ascii ascii);
}

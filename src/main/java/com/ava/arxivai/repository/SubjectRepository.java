package com.ava.arxivai.repository;

import com.ava.arxivai.domain.Subject;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Subject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsSubjectByTitle(String title);
    Subject findSubjectByTitle(String title);
}

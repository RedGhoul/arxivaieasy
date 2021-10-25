package com.ava.arxivai.repository;

import com.ava.arxivai.domain.Paper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Paper entity.
 */
@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    @Query(
        value = "select distinct paper from Paper paper left join fetch paper.authors",
        countQuery = "select count(distinct paper) from Paper paper"
    )
    Page<Paper> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct paper from Paper paper left join fetch paper.authors")
    List<Paper> findAllWithEagerRelationships();

    @Query("select paper from Paper paper left join fetch paper.authors left join fetch paper.subjects where paper.id =:id")
    Optional<Paper> findOneWithEagerRelationships(@Param("id") Long id);

    boolean existsByAbstractText(String arxivAbstractText);
}

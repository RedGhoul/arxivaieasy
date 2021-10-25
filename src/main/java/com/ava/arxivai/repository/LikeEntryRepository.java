package com.ava.arxivai.repository;

import com.ava.arxivai.domain.LikeEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LikeEntry entity.
 */
@Repository
public interface LikeEntryRepository extends JpaRepository<LikeEntry, Long> {
    @Query(
        value = "select distinct likeEntry from LikeEntry likeEntry left join fetch likeEntry.appUsers left join fetch likeEntry.papers",
        countQuery = "select count(distinct likeEntry) from LikeEntry likeEntry"
    )
    Page<LikeEntry> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct likeEntry from LikeEntry likeEntry left join fetch likeEntry.appUsers left join fetch likeEntry.papers")
    List<LikeEntry> findAllWithEagerRelationships();

    @Query(
        "select likeEntry from LikeEntry likeEntry left join fetch likeEntry.appUsers left join fetch likeEntry.papers where likeEntry.id =:id"
    )
    Optional<LikeEntry> findOneWithEagerRelationships(@Param("id") Long id);
}

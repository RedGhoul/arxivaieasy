package com.ava.arxivai.web.rest;

import com.ava.arxivai.domain.LikeEntry;
import com.ava.arxivai.repository.LikeEntryRepository;
import com.ava.arxivai.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ava.arxivai.domain.LikeEntry}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LikeEntryResource {

    private final Logger log = LoggerFactory.getLogger(LikeEntryResource.class);

    private static final String ENTITY_NAME = "likeEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LikeEntryRepository likeEntryRepository;

    public LikeEntryResource(LikeEntryRepository likeEntryRepository) {
        this.likeEntryRepository = likeEntryRepository;
    }

    /**
     * {@code POST  /like-entries} : Create a new likeEntry.
     *
     * @param likeEntry the likeEntry to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new likeEntry, or with status {@code 400 (Bad Request)} if the likeEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/like-entries")
    public ResponseEntity<LikeEntry> createLikeEntry(@RequestBody LikeEntry likeEntry) throws URISyntaxException {
        log.debug("REST request to save LikeEntry : {}", likeEntry);
        if (likeEntry.getId() != null) {
            throw new BadRequestAlertException("A new likeEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LikeEntry result = likeEntryRepository.save(likeEntry);
        return ResponseEntity
            .created(new URI("/api/like-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /like-entries/:id} : Updates an existing likeEntry.
     *
     * @param id the id of the likeEntry to save.
     * @param likeEntry the likeEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated likeEntry,
     * or with status {@code 400 (Bad Request)} if the likeEntry is not valid,
     * or with status {@code 500 (Internal Server Error)} if the likeEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/like-entries/{id}")
    public ResponseEntity<LikeEntry> updateLikeEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LikeEntry likeEntry
    ) throws URISyntaxException {
        log.debug("REST request to update LikeEntry : {}, {}", id, likeEntry);
        if (likeEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, likeEntry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!likeEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LikeEntry result = likeEntryRepository.save(likeEntry);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, likeEntry.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /like-entries/:id} : Partial updates given fields of an existing likeEntry, field will ignore if it is null
     *
     * @param id the id of the likeEntry to save.
     * @param likeEntry the likeEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated likeEntry,
     * or with status {@code 400 (Bad Request)} if the likeEntry is not valid,
     * or with status {@code 404 (Not Found)} if the likeEntry is not found,
     * or with status {@code 500 (Internal Server Error)} if the likeEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/like-entries/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<LikeEntry> partialUpdateLikeEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LikeEntry likeEntry
    ) throws URISyntaxException {
        log.debug("REST request to partial update LikeEntry partially : {}, {}", id, likeEntry);
        if (likeEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, likeEntry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!likeEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LikeEntry> result = likeEntryRepository
            .findById(likeEntry.getId())
            .map(
                existingLikeEntry -> {
                    if (likeEntry.getCreateDate() != null) {
                        existingLikeEntry.setCreateDate(likeEntry.getCreateDate());
                    }

                    return existingLikeEntry;
                }
            )
            .map(likeEntryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, likeEntry.getId().toString())
        );
    }

    /**
     * {@code GET  /like-entries} : get all the likeEntries.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of likeEntries in body.
     */
    @GetMapping("/like-entries")
    public ResponseEntity<List<LikeEntry>> getAllLikeEntries(
        Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of LikeEntries");
        Page<LikeEntry> page;
        if (eagerload) {
            page = likeEntryRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = likeEntryRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /like-entries/:id} : get the "id" likeEntry.
     *
     * @param id the id of the likeEntry to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the likeEntry, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/like-entries/{id}")
    public ResponseEntity<LikeEntry> getLikeEntry(@PathVariable Long id) {
        log.debug("REST request to get LikeEntry : {}", id);
        Optional<LikeEntry> likeEntry = likeEntryRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(likeEntry);
    }

    /**
     * {@code DELETE  /like-entries/:id} : delete the "id" likeEntry.
     *
     * @param id the id of the likeEntry to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/like-entries/{id}")
    public ResponseEntity<Void> deleteLikeEntry(@PathVariable Long id) {
        log.debug("REST request to delete LikeEntry : {}", id);
        likeEntryRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

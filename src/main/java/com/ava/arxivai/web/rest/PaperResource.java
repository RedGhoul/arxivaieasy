package com.ava.arxivai.web.rest;

import com.ava.arxivai.domain.Paper;
import com.ava.arxivai.repository.PaperRepository;
import com.ava.arxivai.web.rest.errors.BadRequestAlertException;
import io.swagger.annotations.Authorization;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ava.arxivai.domain.Paper}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PaperResource {

    private final Logger log = LoggerFactory.getLogger(PaperResource.class);

    private static final String ENTITY_NAME = "paper";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaperRepository paperRepository;

    public PaperResource(PaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    /**
     * {@code POST  /papers} : Create a new paper.
     *
     * @param paper the paper to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paper, or with status {@code 400 (Bad Request)} if the paper has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/papers")
    public ResponseEntity<Paper> createPaper(@Valid @RequestBody Paper paper) throws URISyntaxException {
        log.debug("REST request to save Paper : {}", paper);
        if (paper.getId() != null) {
            throw new BadRequestAlertException("A new paper cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Paper result = paperRepository.save(paper);
        return ResponseEntity
            .created(new URI("/api/papers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /papers/:id} : Updates an existing paper.
     *
     * @param id the id of the paper to save.
     * @param paper the paper to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paper,
     * or with status {@code 400 (Bad Request)} if the paper is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paper couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/papers/{id}")
    public ResponseEntity<Paper> updatePaper(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Paper paper)
        throws URISyntaxException {
        log.debug("REST request to update Paper : {}, {}", id, paper);
        if (paper.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paper.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paperRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Paper result = paperRepository.save(paper);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, paper.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /papers/:id} : Partial updates given fields of an existing paper, field will ignore if it is null
     *
     * @param id the id of the paper to save.
     * @param paper the paper to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paper,
     * or with status {@code 400 (Bad Request)} if the paper is not valid,
     * or with status {@code 404 (Not Found)} if the paper is not found,
     * or with status {@code 500 (Internal Server Error)} if the paper couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/papers/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Paper> partialUpdatePaper(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Paper paper
    ) throws URISyntaxException {
        log.debug("REST request to partial update Paper partially : {}, {}", id, paper);
        if (paper.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paper.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paperRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Paper> result = paperRepository
            .findById(paper.getId())
            .map(
                existingPaper -> {
                    if (paper.getTitle() != null) {
                        existingPaper.setTitle(paper.getTitle());
                    }
                    if (paper.getAbstractText() != null) {
                        existingPaper.setAbstractText(paper.getAbstractText());
                    }
                    if (paper.getSubmitedDate() != null) {
                        existingPaper.setSubmitedDate(paper.getSubmitedDate());
                    }
                    if (paper.getCreatedDate() != null) {
                        existingPaper.setCreatedDate(paper.getCreatedDate());
                    }
                    if (paper.getPdfLink() != null) {
                        existingPaper.setPdfLink(paper.getPdfLink());
                    }
                    if (paper.getBaseLink() != null) {
                        existingPaper.setBaseLink(paper.getBaseLink());
                    }

                    return existingPaper;
                }
            )
            .map(paperRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, paper.getId().toString())
        );
    }

    /**
     * {@code GET  /papers} : get all the papers.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of papers in body.
     */
    @GetMapping("/papers")
    public ResponseEntity<List<Paper>> getAllPapers(
        Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Papers");
        Page<Paper> page;
        if (eagerload) {
            page = paperRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = paperRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/papers/public")
    public ResponseEntity<List<Paper>> getAllPapers(Pageable pageable, @RequestParam(required = false) String find) {
        log.debug("REST request to get a page of Papers");
        Page<Paper> page = paperRepository.findAllWith(find, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /papers/:id} : get the "id" paper.
     *
     * @param id the id of the paper to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paper, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/papers/{id}")
    public ResponseEntity<Paper> getPaper(@PathVariable Long id) {
        log.debug("REST request to get Paper : {}", id);
        Optional<Paper> paper = paperRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(paper);
    }

    /**
     * {@code GET  /papers/:id} : get the "id" paper.
     *
     * @param id the id of the paper to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paper, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/papers/public/{id}")
    public ResponseEntity<Paper> getPaperPublic(@PathVariable Long id) {
        log.debug("REST request to get Paper : {}", id);
        Optional<Paper> paper = paperRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(paper);
    }

    /**
     * {@code DELETE  /papers/:id} : delete the "id" paper.
     *
     * @param id the id of the paper to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/papers/{id}")
    public ResponseEntity<Void> deletePaper(@PathVariable Long id) {
        log.debug("REST request to delete Paper : {}", id);
        paperRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.ava.arxivai.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ava.arxivai.IntegrationTest;
import com.ava.arxivai.domain.Paper;
import com.ava.arxivai.repository.PaperRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PaperResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PaperResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_ABSTRACT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ABSTRACT_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_SUBMITED_DATE = "10 SEP 2021";
    private static final String UPDATED_SUBMITED_DATE = "11 SEP 2021";

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PDF_LINK = "AAAAAAAAAA";
    private static final String UPDATED_PDF_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_BASE_LINK = "AAAAAAAAAA";
    private static final String UPDATED_BASE_LINK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/papers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaperRepository paperRepository;

    @Mock
    private PaperRepository paperRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaperMockMvc;

    private Paper paper;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paper createEntity(EntityManager em) {
        Paper paper = new Paper()
            .title(DEFAULT_TITLE)
            .abstractText(DEFAULT_ABSTRACT_TEXT)
            .submitedDate(DEFAULT_SUBMITED_DATE)
            .createdDate(DEFAULT_CREATED_DATE)
            .pdfLink(DEFAULT_PDF_LINK)
            .baseLink(DEFAULT_BASE_LINK);
        return paper;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paper createUpdatedEntity(EntityManager em) {
        Paper paper = new Paper()
            .title(UPDATED_TITLE)
            .abstractText(UPDATED_ABSTRACT_TEXT)
            .submitedDate(UPDATED_SUBMITED_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .pdfLink(UPDATED_PDF_LINK)
            .baseLink(UPDATED_BASE_LINK);
        return paper;
    }

    @BeforeEach
    public void initTest() {
        paper = createEntity(em);
    }

    @Test
    @Transactional
    void createPaper() throws Exception {
        int databaseSizeBeforeCreate = paperRepository.findAll().size();
        // Create the Paper
        restPaperMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paper)))
            .andExpect(status().isCreated());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeCreate + 1);
        Paper testPaper = paperList.get(paperList.size() - 1);
        assertThat(testPaper.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPaper.getAbstractText()).isEqualTo(DEFAULT_ABSTRACT_TEXT);
        assertThat(testPaper.getSubmitedDate()).isEqualTo(DEFAULT_SUBMITED_DATE);
        assertThat(testPaper.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testPaper.getPdfLink()).isEqualTo(DEFAULT_PDF_LINK);
        assertThat(testPaper.getBaseLink()).isEqualTo(DEFAULT_BASE_LINK);
    }

    @Test
    @Transactional
    void createPaperWithExistingId() throws Exception {
        // Create the Paper with an existing ID
        paper.setId(1L);

        int databaseSizeBeforeCreate = paperRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaperMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paper)))
            .andExpect(status().isBadRequest());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = paperRepository.findAll().size();
        // set the field null
        paper.setTitle(null);

        // Create the Paper, which fails.

        restPaperMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paper)))
            .andExpect(status().isBadRequest());

        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPdfLinkIsRequired() throws Exception {
        int databaseSizeBeforeTest = paperRepository.findAll().size();
        // set the field null
        paper.setPdfLink(null);

        // Create the Paper, which fails.

        restPaperMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paper)))
            .andExpect(status().isBadRequest());

        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBaseLinkIsRequired() throws Exception {
        int databaseSizeBeforeTest = paperRepository.findAll().size();
        // set the field null
        paper.setBaseLink(null);

        // Create the Paper, which fails.

        restPaperMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paper)))
            .andExpect(status().isBadRequest());

        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPapers() throws Exception {
        // Initialize the database
        paperRepository.saveAndFlush(paper);

        // Get all the paperList
        restPaperMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paper.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].abstractText").value(hasItem(DEFAULT_ABSTRACT_TEXT.toString())))
            .andExpect(jsonPath("$.[*].submitedDate").value(hasItem(DEFAULT_SUBMITED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].pdfLink").value(hasItem(DEFAULT_PDF_LINK)))
            .andExpect(jsonPath("$.[*].baseLink").value(hasItem(DEFAULT_BASE_LINK)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPapersWithEagerRelationshipsIsEnabled() throws Exception {
        when(paperRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaperMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(paperRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPapersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(paperRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaperMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(paperRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPaper() throws Exception {
        // Initialize the database
        paperRepository.saveAndFlush(paper);

        // Get the paper
        restPaperMockMvc
            .perform(get(ENTITY_API_URL_ID, paper.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paper.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.abstractText").value(DEFAULT_ABSTRACT_TEXT.toString()))
            .andExpect(jsonPath("$.submitedDate").value(DEFAULT_SUBMITED_DATE.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.pdfLink").value(DEFAULT_PDF_LINK))
            .andExpect(jsonPath("$.baseLink").value(DEFAULT_BASE_LINK));
    }

    @Test
    @Transactional
    void getNonExistingPaper() throws Exception {
        // Get the paper
        restPaperMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPaper() throws Exception {
        // Initialize the database
        paperRepository.saveAndFlush(paper);

        int databaseSizeBeforeUpdate = paperRepository.findAll().size();

        // Update the paper
        Paper updatedPaper = paperRepository.findById(paper.getId()).get();
        // Disconnect from session so that the updates on updatedPaper are not directly saved in db
        em.detach(updatedPaper);
        updatedPaper
            .title(UPDATED_TITLE)
            .abstractText(UPDATED_ABSTRACT_TEXT)
            .submitedDate(UPDATED_SUBMITED_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .pdfLink(UPDATED_PDF_LINK)
            .baseLink(UPDATED_BASE_LINK);

        restPaperMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPaper.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPaper))
            )
            .andExpect(status().isOk());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
        Paper testPaper = paperList.get(paperList.size() - 1);
        assertThat(testPaper.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPaper.getAbstractText()).isEqualTo(UPDATED_ABSTRACT_TEXT);
        assertThat(testPaper.getSubmitedDate()).isEqualTo(UPDATED_SUBMITED_DATE);
        assertThat(testPaper.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testPaper.getPdfLink()).isEqualTo(UPDATED_PDF_LINK);
        assertThat(testPaper.getBaseLink()).isEqualTo(UPDATED_BASE_LINK);
    }

    @Test
    @Transactional
    void putNonExistingPaper() throws Exception {
        int databaseSizeBeforeUpdate = paperRepository.findAll().size();
        paper.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaperMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paper.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paper))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaper() throws Exception {
        int databaseSizeBeforeUpdate = paperRepository.findAll().size();
        paper.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaperMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paper))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaper() throws Exception {
        int databaseSizeBeforeUpdate = paperRepository.findAll().size();
        paper.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaperMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paper)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaperWithPatch() throws Exception {
        // Initialize the database
        paperRepository.saveAndFlush(paper);

        int databaseSizeBeforeUpdate = paperRepository.findAll().size();

        // Update the paper using partial update
        Paper partialUpdatedPaper = new Paper();
        partialUpdatedPaper.setId(paper.getId());

        partialUpdatedPaper
            .title(UPDATED_TITLE)
            .abstractText(UPDATED_ABSTRACT_TEXT)
            .submitedDate(UPDATED_SUBMITED_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .baseLink(UPDATED_BASE_LINK);

        restPaperMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaper.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPaper))
            )
            .andExpect(status().isOk());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
        Paper testPaper = paperList.get(paperList.size() - 1);
        assertThat(testPaper.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPaper.getAbstractText()).isEqualTo(UPDATED_ABSTRACT_TEXT);
        assertThat(testPaper.getSubmitedDate()).isEqualTo(UPDATED_SUBMITED_DATE);
        assertThat(testPaper.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testPaper.getPdfLink()).isEqualTo(DEFAULT_PDF_LINK);
        assertThat(testPaper.getBaseLink()).isEqualTo(UPDATED_BASE_LINK);
    }

    @Test
    @Transactional
    void fullUpdatePaperWithPatch() throws Exception {
        // Initialize the database
        paperRepository.saveAndFlush(paper);

        int databaseSizeBeforeUpdate = paperRepository.findAll().size();

        // Update the paper using partial update
        Paper partialUpdatedPaper = new Paper();
        partialUpdatedPaper.setId(paper.getId());

        partialUpdatedPaper
            .title(UPDATED_TITLE)
            .abstractText(UPDATED_ABSTRACT_TEXT)
            .submitedDate(UPDATED_SUBMITED_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .pdfLink(UPDATED_PDF_LINK)
            .baseLink(UPDATED_BASE_LINK);

        restPaperMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaper.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPaper))
            )
            .andExpect(status().isOk());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
        Paper testPaper = paperList.get(paperList.size() - 1);
        assertThat(testPaper.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPaper.getAbstractText()).isEqualTo(UPDATED_ABSTRACT_TEXT);
        assertThat(testPaper.getSubmitedDate()).isEqualTo(UPDATED_SUBMITED_DATE);
        assertThat(testPaper.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testPaper.getPdfLink()).isEqualTo(UPDATED_PDF_LINK);
        assertThat(testPaper.getBaseLink()).isEqualTo(UPDATED_BASE_LINK);
    }

    @Test
    @Transactional
    void patchNonExistingPaper() throws Exception {
        int databaseSizeBeforeUpdate = paperRepository.findAll().size();
        paper.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaperMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paper.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paper))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaper() throws Exception {
        int databaseSizeBeforeUpdate = paperRepository.findAll().size();
        paper.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaperMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paper))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaper() throws Exception {
        int databaseSizeBeforeUpdate = paperRepository.findAll().size();
        paper.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaperMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(paper)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Paper in the database
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaper() throws Exception {
        // Initialize the database
        paperRepository.saveAndFlush(paper);

        int databaseSizeBeforeDelete = paperRepository.findAll().size();

        // Delete the paper
        restPaperMockMvc
            .perform(delete(ENTITY_API_URL_ID, paper.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Paper> paperList = paperRepository.findAll();
        assertThat(paperList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

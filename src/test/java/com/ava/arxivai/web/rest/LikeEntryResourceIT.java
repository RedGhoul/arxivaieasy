package com.ava.arxivai.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ava.arxivai.IntegrationTest;
import com.ava.arxivai.domain.LikeEntry;
import com.ava.arxivai.repository.LikeEntryRepository;
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

/**
 * Integration tests for the {@link LikeEntryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LikeEntryResourceIT {

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/like-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LikeEntryRepository likeEntryRepository;

    @Mock
    private LikeEntryRepository likeEntryRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLikeEntryMockMvc;

    private LikeEntry likeEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LikeEntry createEntity(EntityManager em) {
        LikeEntry likeEntry = new LikeEntry().createDate(DEFAULT_CREATE_DATE);
        return likeEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LikeEntry createUpdatedEntity(EntityManager em) {
        LikeEntry likeEntry = new LikeEntry().createDate(UPDATED_CREATE_DATE);
        return likeEntry;
    }

    @BeforeEach
    public void initTest() {
        likeEntry = createEntity(em);
    }

    @Test
    @Transactional
    void createLikeEntry() throws Exception {
        int databaseSizeBeforeCreate = likeEntryRepository.findAll().size();
        // Create the LikeEntry
        restLikeEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(likeEntry)))
            .andExpect(status().isCreated());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeCreate + 1);
        LikeEntry testLikeEntry = likeEntryList.get(likeEntryList.size() - 1);
        assertThat(testLikeEntry.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
    }

    @Test
    @Transactional
    void createLikeEntryWithExistingId() throws Exception {
        // Create the LikeEntry with an existing ID
        likeEntry.setId(1L);

        int databaseSizeBeforeCreate = likeEntryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLikeEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(likeEntry)))
            .andExpect(status().isBadRequest());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLikeEntries() throws Exception {
        // Initialize the database
        likeEntryRepository.saveAndFlush(likeEntry);

        // Get all the likeEntryList
        restLikeEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(likeEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLikeEntriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(likeEntryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLikeEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(likeEntryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLikeEntriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(likeEntryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLikeEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(likeEntryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getLikeEntry() throws Exception {
        // Initialize the database
        likeEntryRepository.saveAndFlush(likeEntry);

        // Get the likeEntry
        restLikeEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, likeEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(likeEntry.getId().intValue()))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLikeEntry() throws Exception {
        // Get the likeEntry
        restLikeEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLikeEntry() throws Exception {
        // Initialize the database
        likeEntryRepository.saveAndFlush(likeEntry);

        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();

        // Update the likeEntry
        LikeEntry updatedLikeEntry = likeEntryRepository.findById(likeEntry.getId()).get();
        // Disconnect from session so that the updates on updatedLikeEntry are not directly saved in db
        em.detach(updatedLikeEntry);
        updatedLikeEntry.createDate(UPDATED_CREATE_DATE);

        restLikeEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLikeEntry.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLikeEntry))
            )
            .andExpect(status().isOk());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
        LikeEntry testLikeEntry = likeEntryList.get(likeEntryList.size() - 1);
        assertThat(testLikeEntry.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    void putNonExistingLikeEntry() throws Exception {
        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();
        likeEntry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLikeEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, likeEntry.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(likeEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLikeEntry() throws Exception {
        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();
        likeEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLikeEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(likeEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLikeEntry() throws Exception {
        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();
        likeEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLikeEntryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(likeEntry)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLikeEntryWithPatch() throws Exception {
        // Initialize the database
        likeEntryRepository.saveAndFlush(likeEntry);

        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();

        // Update the likeEntry using partial update
        LikeEntry partialUpdatedLikeEntry = new LikeEntry();
        partialUpdatedLikeEntry.setId(likeEntry.getId());

        restLikeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLikeEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLikeEntry))
            )
            .andExpect(status().isOk());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
        LikeEntry testLikeEntry = likeEntryList.get(likeEntryList.size() - 1);
        assertThat(testLikeEntry.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
    }

    @Test
    @Transactional
    void fullUpdateLikeEntryWithPatch() throws Exception {
        // Initialize the database
        likeEntryRepository.saveAndFlush(likeEntry);

        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();

        // Update the likeEntry using partial update
        LikeEntry partialUpdatedLikeEntry = new LikeEntry();
        partialUpdatedLikeEntry.setId(likeEntry.getId());

        partialUpdatedLikeEntry.createDate(UPDATED_CREATE_DATE);

        restLikeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLikeEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLikeEntry))
            )
            .andExpect(status().isOk());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
        LikeEntry testLikeEntry = likeEntryList.get(likeEntryList.size() - 1);
        assertThat(testLikeEntry.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingLikeEntry() throws Exception {
        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();
        likeEntry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLikeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, likeEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(likeEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLikeEntry() throws Exception {
        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();
        likeEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLikeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(likeEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLikeEntry() throws Exception {
        int databaseSizeBeforeUpdate = likeEntryRepository.findAll().size();
        likeEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLikeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(likeEntry))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LikeEntry in the database
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLikeEntry() throws Exception {
        // Initialize the database
        likeEntryRepository.saveAndFlush(likeEntry);

        int databaseSizeBeforeDelete = likeEntryRepository.findAll().size();

        // Delete the likeEntry
        restLikeEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, likeEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LikeEntry> likeEntryList = likeEntryRepository.findAll();
        assertThat(likeEntryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

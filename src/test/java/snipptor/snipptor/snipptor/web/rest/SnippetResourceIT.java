package snipptor.snipptor.snipptor.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import snipptor.snipptor.snipptor.IntegrationTest;
import snipptor.snipptor.snipptor.domain.Snippet;
import snipptor.snipptor.snipptor.domain.enumeration.SnippetClassification;
import snipptor.snipptor.snipptor.repository.SnippetRepository;

/**
 * Integration tests for the {@link SnippetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SnippetResourceIT {

    private static final String DEFAULT_HASH = "AAAAAAAAAA";
    private static final String UPDATED_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final SnippetClassification DEFAULT_CLASSIFICATION = SnippetClassification.UNKNOWN;
    private static final SnippetClassification UPDATED_CLASSIFICATION = SnippetClassification.SAFE;

    private static final Long DEFAULT_SCAN_COUNT = 1L;
    private static final Long UPDATED_SCAN_COUNT = 2L;

    private static final String ENTITY_API_URL = "/api/snippets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SnippetRepository snippetRepository;

    @Mock
    private SnippetRepository snippetRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSnippetMockMvc;

    private Snippet snippet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Snippet createEntity(EntityManager em) {
        Snippet snippet = new Snippet()
            .hash(DEFAULT_HASH)
            .content(DEFAULT_CONTENT)
            .url(DEFAULT_URL)
            .classification(DEFAULT_CLASSIFICATION)
            .scanCount(DEFAULT_SCAN_COUNT);
        return snippet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Snippet createUpdatedEntity(EntityManager em) {
        Snippet snippet = new Snippet()
            .hash(UPDATED_HASH)
            .content(UPDATED_CONTENT)
            .url(UPDATED_URL)
            .classification(UPDATED_CLASSIFICATION)
            .scanCount(UPDATED_SCAN_COUNT);
        return snippet;
    }

    @BeforeEach
    public void initTest() {
        snippet = createEntity(em);
    }

    @Test
    @Transactional
    void createSnippet() throws Exception {
        int databaseSizeBeforeCreate = snippetRepository.findAll().size();
        // Create the Snippet
        restSnippetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(snippet)))
            .andExpect(status().isCreated());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeCreate + 1);
        Snippet testSnippet = snippetList.get(snippetList.size() - 1);
        assertThat(testSnippet.getHash()).isEqualTo(DEFAULT_HASH);
        assertThat(testSnippet.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testSnippet.getClassification()).isEqualTo(DEFAULT_CLASSIFICATION);
        assertThat(testSnippet.getScanCount()).isEqualTo(DEFAULT_SCAN_COUNT);
    }

    @Test
    @Transactional
    void createSnippetWithExistingId() throws Exception {
        // Create the Snippet with an existing ID
        snippet.setId(1L);

        int databaseSizeBeforeCreate = snippetRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSnippetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(snippet)))
            .andExpect(status().isBadRequest());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSnippets() throws Exception {
        // Initialize the database
        snippetRepository.saveAndFlush(snippet);

        // Get all the snippetList
        restSnippetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(snippet.getId().intValue())))
            .andExpect(jsonPath("$.[*].hash").value(hasItem(DEFAULT_HASH)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].classification").value(hasItem(DEFAULT_CLASSIFICATION.toString())))
            .andExpect(jsonPath("$.[*].scanCount").value(hasItem(DEFAULT_SCAN_COUNT.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSnippetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(snippetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSnippetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(snippetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSnippetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(snippetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSnippetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(snippetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getSnippet() throws Exception {
        // Initialize the database
        snippetRepository.saveAndFlush(snippet);

        // Get the snippet
        restSnippetMockMvc
            .perform(get(ENTITY_API_URL_ID, snippet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(snippet.getId().intValue()))
            .andExpect(jsonPath("$.hash").value(DEFAULT_HASH))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.classification").value(DEFAULT_CLASSIFICATION.toString()))
            .andExpect(jsonPath("$.scanCount").value(DEFAULT_SCAN_COUNT.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingSnippet() throws Exception {
        // Get the snippet
        restSnippetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSnippet() throws Exception {
        // Initialize the database
        snippetRepository.saveAndFlush(snippet);

        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();

        // Update the snippet
        Snippet updatedSnippet = snippetRepository.findById(snippet.getId()).get();
        // Disconnect from session so that the updates on updatedSnippet are not directly saved in db
        em.detach(updatedSnippet);
        updatedSnippet
            .hash(UPDATED_HASH)
            .content(UPDATED_CONTENT)
            .url(UPDATED_URL)
            .classification(UPDATED_CLASSIFICATION)
            .scanCount(UPDATED_SCAN_COUNT);

        restSnippetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSnippet.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSnippet))
            )
            .andExpect(status().isOk());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
        Snippet testSnippet = snippetList.get(snippetList.size() - 1);
        assertThat(testSnippet.getHash()).isEqualTo(UPDATED_HASH);
        assertThat(testSnippet.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testSnippet.getClassification()).isEqualTo(UPDATED_CLASSIFICATION);
        assertThat(testSnippet.getScanCount()).isEqualTo(UPDATED_SCAN_COUNT);
    }

    @Test
    @Transactional
    void putNonExistingSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();
        snippet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSnippetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, snippet.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(snippet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();
        snippet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSnippetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(snippet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();
        snippet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSnippetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(snippet)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSnippetWithPatch() throws Exception {
        // Initialize the database
        snippetRepository.saveAndFlush(snippet);

        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();

        // Update the snippet using partial update
        Snippet partialUpdatedSnippet = new Snippet();
        partialUpdatedSnippet.setId(snippet.getId());

        partialUpdatedSnippet.scanCount(UPDATED_SCAN_COUNT);

        restSnippetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSnippet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSnippet))
            )
            .andExpect(status().isOk());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
        Snippet testSnippet = snippetList.get(snippetList.size() - 1);
        assertThat(testSnippet.getHash()).isEqualTo(DEFAULT_HASH);
        assertThat(testSnippet.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testSnippet.getClassification()).isEqualTo(DEFAULT_CLASSIFICATION);
        assertThat(testSnippet.getScanCount()).isEqualTo(UPDATED_SCAN_COUNT);
    }

    @Test
    @Transactional
    void fullUpdateSnippetWithPatch() throws Exception {
        // Initialize the database
        snippetRepository.saveAndFlush(snippet);

        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();

        // Update the snippet using partial update
        Snippet partialUpdatedSnippet = new Snippet();
        partialUpdatedSnippet.setId(snippet.getId());

        partialUpdatedSnippet
            .hash(UPDATED_HASH)
            .content(UPDATED_CONTENT)
            .url(UPDATED_URL)
            .classification(UPDATED_CLASSIFICATION)
            .scanCount(UPDATED_SCAN_COUNT);

        restSnippetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSnippet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSnippet))
            )
            .andExpect(status().isOk());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
        Snippet testSnippet = snippetList.get(snippetList.size() - 1);
        assertThat(testSnippet.getHash()).isEqualTo(UPDATED_HASH);
        assertThat(testSnippet.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testSnippet.getClassification()).isEqualTo(UPDATED_CLASSIFICATION);
        assertThat(testSnippet.getScanCount()).isEqualTo(UPDATED_SCAN_COUNT);
    }

    @Test
    @Transactional
    void patchNonExistingSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();
        snippet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSnippetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, snippet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(snippet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();
        snippet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSnippetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(snippet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().size();
        snippet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSnippetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(snippet)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSnippet() throws Exception {
        // Initialize the database
        snippetRepository.saveAndFlush(snippet);

        int databaseSizeBeforeDelete = snippetRepository.findAll().size();

        // Delete the snippet
        restSnippetMockMvc
            .perform(delete(ENTITY_API_URL_ID, snippet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Snippet> snippetList = snippetRepository.findAll();
        assertThat(snippetList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

package snipptor.snipptor.snipptor.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.IntegrationTest;
import snipptor.snipptor.snipptor.domain.Snippet;
import snipptor.snipptor.snipptor.repository.EntityManager;
import snipptor.snipptor.snipptor.repository.SnippetRepository;

/**
 * Integration tests for the {@link SnippetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SnippetResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

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
    private WebTestClient webTestClient;

    private Snippet snippet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Snippet createEntity(EntityManager em) {
        Snippet snippet = new Snippet().content(DEFAULT_CONTENT).url(DEFAULT_URL);
        return snippet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Snippet createUpdatedEntity(EntityManager em) {
        Snippet snippet = new Snippet().content(UPDATED_CONTENT).url(UPDATED_URL);
        return snippet;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_snippet__snippet_matched_rules").block();
            em.deleteAll(Snippet.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        snippet = createEntity(em);
    }

    @Test
    void createSnippet() throws Exception {
        int databaseSizeBeforeCreate = snippetRepository.findAll().collectList().block().size();
        // Create the Snippet
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeCreate + 1);
        Snippet testSnippet = snippetList.get(snippetList.size() - 1);
        assertThat(testSnippet.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    void createSnippetWithExistingId() throws Exception {
        // Create the Snippet with an existing ID
        snippet.setId(1L);

        int databaseSizeBeforeCreate = snippetRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = snippetRepository.findAll().collectList().block().size();
        // set the field null
        snippet.setContent(null);

        // Create the Snippet, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSnippetsAsStream() {
        // Initialize the database
        snippetRepository.save(snippet).block();

        List<Snippet> snippetList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Snippet.class)
            .getResponseBody()
            .filter(snippet::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(snippetList).isNotNull();
        assertThat(snippetList).hasSize(1);
        Snippet testSnippet = snippetList.get(0);
        assertThat(testSnippet.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    void getAllSnippets() {
        // Initialize the database
        snippetRepository.save(snippet).block();

        // Get all the snippetList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(snippet.getId().intValue()))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSnippetsWithEagerRelationshipsIsEnabled() {
        when(snippetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(snippetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSnippetsWithEagerRelationshipsIsNotEnabled() {
        when(snippetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(snippetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSnippet() {
        // Initialize the database
        snippetRepository.save(snippet).block();

        // Get the snippet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, snippet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(snippet.getId().intValue()))
            .jsonPath("$.content")
            .value(is(DEFAULT_CONTENT))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL));
    }

    @Test
    void getNonExistingSnippet() {
        // Get the snippet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSnippet() throws Exception {
        // Initialize the database
        snippetRepository.save(snippet).block();

        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();

        // Update the snippet
        Snippet updatedSnippet = snippetRepository.findById(snippet.getId()).block();
        updatedSnippet.content(UPDATED_CONTENT).url(UPDATED_URL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSnippet.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSnippet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
        Snippet testSnippet = snippetList.get(snippetList.size() - 1);
        assertThat(testSnippet.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void putNonExistingSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();
        snippet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, snippet.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();
        snippet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();
        snippet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSnippetWithPatch() throws Exception {
        // Initialize the database
        snippetRepository.save(snippet).block();

        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();

        // Update the snippet using partial update
        Snippet partialUpdatedSnippet = new Snippet();
        partialUpdatedSnippet.setId(snippet.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSnippet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSnippet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
        Snippet testSnippet = snippetList.get(snippetList.size() - 1);
        assertThat(testSnippet.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    void fullUpdateSnippetWithPatch() throws Exception {
        // Initialize the database
        snippetRepository.save(snippet).block();

        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();

        // Update the snippet using partial update
        Snippet partialUpdatedSnippet = new Snippet();
        partialUpdatedSnippet.setId(snippet.getId());

        partialUpdatedSnippet.content(UPDATED_CONTENT).url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSnippet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSnippet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
        Snippet testSnippet = snippetList.get(snippetList.size() - 1);
        assertThat(testSnippet.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testSnippet.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    void patchNonExistingSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();
        snippet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, snippet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();
        snippet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSnippet() throws Exception {
        int databaseSizeBeforeUpdate = snippetRepository.findAll().collectList().block().size();
        snippet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(snippet))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Snippet in the database
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSnippet() {
        // Initialize the database
        snippetRepository.save(snippet).block();

        int databaseSizeBeforeDelete = snippetRepository.findAll().collectList().block().size();

        // Delete the snippet
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, snippet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Snippet> snippetList = snippetRepository.findAll().collectList().block();
        assertThat(snippetList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

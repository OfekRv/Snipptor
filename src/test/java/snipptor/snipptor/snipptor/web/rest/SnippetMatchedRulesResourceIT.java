package snipptor.snipptor.snipptor.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import snipptor.snipptor.snipptor.IntegrationTest;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.repository.EntityManager;
import snipptor.snipptor.snipptor.repository.SnippetMatchedRulesRepository;

/**
 * Integration tests for the {@link SnippetMatchedRulesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SnippetMatchedRulesResourceIT {

    private static final String ENTITY_API_URL = "/api/snippet-matched-rules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SnippetMatchedRulesRepository snippetMatchedRulesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private SnippetMatchedRules snippetMatchedRules;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SnippetMatchedRules createEntity(EntityManager em) {
        SnippetMatchedRules snippetMatchedRules = new SnippetMatchedRules();
        return snippetMatchedRules;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SnippetMatchedRules createUpdatedEntity(EntityManager em) {
        SnippetMatchedRules snippetMatchedRules = new SnippetMatchedRules();
        return snippetMatchedRules;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(SnippetMatchedRules.class).block();
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
        snippetMatchedRules = createEntity(em);
    }

    @Test
    void getAllSnippetMatchedRules() {
        // Initialize the database
        snippetMatchedRulesRepository.save(snippetMatchedRules).block();

        // Get all the snippetMatchedRulesList
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
            .value(hasItem(snippetMatchedRules.getId().intValue()));
    }

    @Test
    void getSnippetMatchedRules() {
        // Initialize the database
        snippetMatchedRulesRepository.save(snippetMatchedRules).block();

        // Get the snippetMatchedRules
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, snippetMatchedRules.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(snippetMatchedRules.getId().intValue()));
    }

    @Test
    void getNonExistingSnippetMatchedRules() {
        // Get the snippetMatchedRules
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }
}

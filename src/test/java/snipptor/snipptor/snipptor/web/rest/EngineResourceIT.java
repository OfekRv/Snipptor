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
import snipptor.snipptor.snipptor.domain.Engine;
import snipptor.snipptor.snipptor.repository.EngineRepository;
import snipptor.snipptor.snipptor.repository.EntityManager;

/**
 * Integration tests for the {@link EngineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EngineResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/engines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EngineRepository engineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Engine engine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engine createEntity(EntityManager em) {
        Engine engine = new Engine().name(DEFAULT_NAME);
        return engine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engine createUpdatedEntity(EntityManager em) {
        Engine engine = new Engine().name(UPDATED_NAME);
        return engine;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Engine.class).block();
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
        engine = createEntity(em);
    }

    @Test
    void createEngine() throws Exception {
        int databaseSizeBeforeCreate = engineRepository.findAll().collectList().block().size();
        // Create the Engine
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeCreate + 1);
        Engine testEngine = engineList.get(engineList.size() - 1);
        assertThat(testEngine.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createEngineWithExistingId() throws Exception {
        // Create the Engine with an existing ID
        engine.setId(1L);

        int databaseSizeBeforeCreate = engineRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = engineRepository.findAll().collectList().block().size();
        // set the field null
        engine.setName(null);

        // Create the Engine, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllEngines() {
        // Initialize the database
        engineRepository.save(engine).block();

        // Get all the engineList
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
            .value(hasItem(engine.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getEngine() {
        // Initialize the database
        engineRepository.save(engine).block();

        // Get the engine
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, engine.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(engine.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingEngine() {
        // Get the engine
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEngine() throws Exception {
        // Initialize the database
        engineRepository.save(engine).block();

        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();

        // Update the engine
        Engine updatedEngine = engineRepository.findById(engine.getId()).block();
        updatedEngine.name(UPDATED_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEngine.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEngine))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
        Engine testEngine = engineList.get(engineList.size() - 1);
        assertThat(testEngine.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();
        engine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, engine.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();
        engine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();
        engine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEngineWithPatch() throws Exception {
        // Initialize the database
        engineRepository.save(engine).block();

        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();

        // Update the engine using partial update
        Engine partialUpdatedEngine = new Engine();
        partialUpdatedEngine.setId(engine.getId());

        partialUpdatedEngine.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEngine.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEngine))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
        Engine testEngine = engineList.get(engineList.size() - 1);
        assertThat(testEngine.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void fullUpdateEngineWithPatch() throws Exception {
        // Initialize the database
        engineRepository.save(engine).block();

        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();

        // Update the engine using partial update
        Engine partialUpdatedEngine = new Engine();
        partialUpdatedEngine.setId(engine.getId());

        partialUpdatedEngine.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEngine.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEngine))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
        Engine testEngine = engineList.get(engineList.size() - 1);
        assertThat(testEngine.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();
        engine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, engine.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();
        engine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().collectList().block().size();
        engine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(engine))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEngine() {
        // Initialize the database
        engineRepository.save(engine).block();

        int databaseSizeBeforeDelete = engineRepository.findAll().collectList().block().size();

        // Delete the engine
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, engine.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Engine> engineList = engineRepository.findAll().collectList().block();
        assertThat(engineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

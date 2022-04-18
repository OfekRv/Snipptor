package snipptor.snipptor.snipptor.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import snipptor.snipptor.snipptor.IntegrationTest;
import snipptor.snipptor.snipptor.domain.Engine;
import snipptor.snipptor.snipptor.repository.EngineRepository;

/**
 * Integration tests for the {@link EngineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EngineResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/engines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EngineRepository engineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEngineMockMvc;

    private Engine engine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engine createEntity(EntityManager em) {
        Engine engine = new Engine().name(DEFAULT_NAME).url(DEFAULT_URL);
        return engine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engine createUpdatedEntity(EntityManager em) {
        Engine engine = new Engine().name(UPDATED_NAME).url(UPDATED_URL);
        return engine;
    }

    @BeforeEach
    public void initTest() {
        engine = createEntity(em);
    }

    @Test
    @Transactional
    void createEngine() throws Exception {
        int databaseSizeBeforeCreate = engineRepository.findAll().size();
        // Create the Engine
        restEngineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engine)))
            .andExpect(status().isCreated());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeCreate + 1);
        Engine testEngine = engineList.get(engineList.size() - 1);
        assertThat(testEngine.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEngine.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void createEngineWithExistingId() throws Exception {
        // Create the Engine with an existing ID
        engine.setId(1L);

        int databaseSizeBeforeCreate = engineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEngineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engine)))
            .andExpect(status().isBadRequest());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = engineRepository.findAll().size();
        // set the field null
        engine.setName(null);

        // Create the Engine, which fails.

        restEngineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engine)))
            .andExpect(status().isBadRequest());

        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = engineRepository.findAll().size();
        // set the field null
        engine.setUrl(null);

        // Create the Engine, which fails.

        restEngineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engine)))
            .andExpect(status().isBadRequest());

        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEngines() throws Exception {
        // Initialize the database
        engineRepository.saveAndFlush(engine);

        // Get all the engineList
        restEngineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(engine.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @Test
    @Transactional
    void getEngine() throws Exception {
        // Initialize the database
        engineRepository.saveAndFlush(engine);

        // Get the engine
        restEngineMockMvc
            .perform(get(ENTITY_API_URL_ID, engine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(engine.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getNonExistingEngine() throws Exception {
        // Get the engine
        restEngineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEngine() throws Exception {
        // Initialize the database
        engineRepository.saveAndFlush(engine);

        int databaseSizeBeforeUpdate = engineRepository.findAll().size();

        // Update the engine
        Engine updatedEngine = engineRepository.findById(engine.getId()).get();
        // Disconnect from session so that the updates on updatedEngine are not directly saved in db
        em.detach(updatedEngine);
        updatedEngine.name(UPDATED_NAME).url(UPDATED_URL);

        restEngineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEngine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEngine))
            )
            .andExpect(status().isOk());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
        Engine testEngine = engineList.get(engineList.size() - 1);
        assertThat(testEngine.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEngine.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void putNonExistingEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().size();
        engine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEngineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, engine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().size();
        engine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEngineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().size();
        engine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEngineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEngineWithPatch() throws Exception {
        // Initialize the database
        engineRepository.saveAndFlush(engine);

        int databaseSizeBeforeUpdate = engineRepository.findAll().size();

        // Update the engine using partial update
        Engine partialUpdatedEngine = new Engine();
        partialUpdatedEngine.setId(engine.getId());

        partialUpdatedEngine.name(UPDATED_NAME);

        restEngineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEngine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEngine))
            )
            .andExpect(status().isOk());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
        Engine testEngine = engineList.get(engineList.size() - 1);
        assertThat(testEngine.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEngine.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void fullUpdateEngineWithPatch() throws Exception {
        // Initialize the database
        engineRepository.saveAndFlush(engine);

        int databaseSizeBeforeUpdate = engineRepository.findAll().size();

        // Update the engine using partial update
        Engine partialUpdatedEngine = new Engine();
        partialUpdatedEngine.setId(engine.getId());

        partialUpdatedEngine.name(UPDATED_NAME).url(UPDATED_URL);

        restEngineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEngine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEngine))
            )
            .andExpect(status().isOk());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
        Engine testEngine = engineList.get(engineList.size() - 1);
        assertThat(testEngine.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEngine.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void patchNonExistingEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().size();
        engine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEngineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, engine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(engine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().size();
        engine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEngineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(engine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEngine() throws Exception {
        int databaseSizeBeforeUpdate = engineRepository.findAll().size();
        engine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEngineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(engine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Engine in the database
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEngine() throws Exception {
        // Initialize the database
        engineRepository.saveAndFlush(engine);

        int databaseSizeBeforeDelete = engineRepository.findAll().size();

        // Delete the engine
        restEngineMockMvc
            .perform(delete(ENTITY_API_URL_ID, engine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Engine> engineList = engineRepository.findAll();
        assertThat(engineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

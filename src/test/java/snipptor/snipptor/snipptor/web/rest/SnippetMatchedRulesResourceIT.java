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
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.repository.SnippetMatchedRulesRepository;

/**
 * Integration tests for the {@link SnippetMatchedRulesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restSnippetMatchedRulesMockMvc;

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

    @BeforeEach
    public void initTest() {
        snippetMatchedRules = createEntity(em);
    }

    @Test
    @Transactional
    void getAllSnippetMatchedRules() throws Exception {
        // Initialize the database
        snippetMatchedRulesRepository.saveAndFlush(snippetMatchedRules);

        // Get all the snippetMatchedRulesList
        restSnippetMatchedRulesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(snippetMatchedRules.getId().intValue())));
    }

    @Test
    @Transactional
    void getSnippetMatchedRules() throws Exception {
        // Initialize the database
        snippetMatchedRulesRepository.saveAndFlush(snippetMatchedRules);

        // Get the snippetMatchedRules
        restSnippetMatchedRulesMockMvc
            .perform(get(ENTITY_API_URL_ID, snippetMatchedRules.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(snippetMatchedRules.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingSnippetMatchedRules() throws Exception {
        // Get the snippetMatchedRules
        restSnippetMatchedRulesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}

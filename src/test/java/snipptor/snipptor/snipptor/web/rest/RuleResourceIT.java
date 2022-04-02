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
import snipptor.snipptor.snipptor.domain.Rule;
import snipptor.snipptor.snipptor.repository.EntityManager;
import snipptor.snipptor.snipptor.repository.RuleRepository;

/**
 * Integration tests for the {@link RuleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RuleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RuleRepository ruleRepository;

    @Mock
    private RuleRepository ruleRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Rule rule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rule createEntity(EntityManager em) {
        Rule rule = new Rule().name(DEFAULT_NAME);
        return rule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rule createUpdatedEntity(EntityManager em) {
        Rule rule = new Rule().name(UPDATED_NAME);
        return rule;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_rule__snippet_matched_rules").block();
            em.deleteAll(Rule.class).block();
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
        rule = createEntity(em);
    }

    @Test
    void createRule() throws Exception {
        int databaseSizeBeforeCreate = ruleRepository.findAll().collectList().block().size();
        // Create the Rule
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeCreate + 1);
        Rule testRule = ruleList.get(ruleList.size() - 1);
        assertThat(testRule.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createRuleWithExistingId() throws Exception {
        // Create the Rule with an existing ID
        rule.setId(1L);

        int databaseSizeBeforeCreate = ruleRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ruleRepository.findAll().collectList().block().size();
        // set the field null
        rule.setName(null);

        // Create the Rule, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRules() {
        // Initialize the database
        ruleRepository.save(rule).block();

        // Get all the ruleList
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
            .value(hasItem(rule.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRulesWithEagerRelationshipsIsEnabled() {
        when(ruleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(ruleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRulesWithEagerRelationshipsIsNotEnabled() {
        when(ruleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(ruleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getRule() {
        // Initialize the database
        ruleRepository.save(rule).block();

        // Get the rule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rule.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingRule() {
        // Get the rule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRule() throws Exception {
        // Initialize the database
        ruleRepository.save(rule).block();

        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();

        // Update the rule
        Rule updatedRule = ruleRepository.findById(rule.getId()).block();
        updatedRule.name(UPDATED_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRule.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
        Rule testRule = ruleList.get(ruleList.size() - 1);
        assertThat(testRule.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingRule() throws Exception {
        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();
        rule.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rule.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRule() throws Exception {
        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();
        rule.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRule() throws Exception {
        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();
        rule.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRuleWithPatch() throws Exception {
        // Initialize the database
        ruleRepository.save(rule).block();

        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();

        // Update the rule using partial update
        Rule partialUpdatedRule = new Rule();
        partialUpdatedRule.setId(rule.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
        Rule testRule = ruleList.get(ruleList.size() - 1);
        assertThat(testRule.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void fullUpdateRuleWithPatch() throws Exception {
        // Initialize the database
        ruleRepository.save(rule).block();

        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();

        // Update the rule using partial update
        Rule partialUpdatedRule = new Rule();
        partialUpdatedRule.setId(rule.getId());

        partialUpdatedRule.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
        Rule testRule = ruleList.get(ruleList.size() - 1);
        assertThat(testRule.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingRule() throws Exception {
        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();
        rule.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRule() throws Exception {
        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();
        rule.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRule() throws Exception {
        int databaseSizeBeforeUpdate = ruleRepository.findAll().collectList().block().size();
        rule.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rule))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rule in the database
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRule() {
        // Initialize the database
        ruleRepository.save(rule).block();

        int databaseSizeBeforeDelete = ruleRepository.findAll().collectList().block().size();

        // Delete the rule
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Rule> ruleList = ruleRepository.findAll().collectList().block();
        assertThat(ruleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

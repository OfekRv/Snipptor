package snipptor.snipptor.snipptor.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.Rule;
import snipptor.snipptor.snipptor.repository.RuleRepository;
import snipptor.snipptor.snipptor.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link snipptor.snipptor.snipptor.domain.Rule}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RuleResource {

    private final Logger log = LoggerFactory.getLogger(RuleResource.class);

    private static final String ENTITY_NAME = "rule";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RuleRepository ruleRepository;

    public RuleResource(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /**
     * {@code POST  /rules} : Create a new rule.
     *
     * @param rule the rule to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rule, or with status {@code 400 (Bad Request)} if the rule has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rules")
    public Mono<ResponseEntity<Rule>> createRule(@Valid @RequestBody Rule rule) throws URISyntaxException {
        log.debug("REST request to save Rule : {}", rule);
        if (rule.getId() != null) {
            throw new BadRequestAlertException("A new rule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ruleRepository
            .save(rule)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/rules/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /rules/:id} : Updates an existing rule.
     *
     * @param id the id of the rule to save.
     * @param rule the rule to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rule,
     * or with status {@code 400 (Bad Request)} if the rule is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rule couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rules/{id}")
    public Mono<ResponseEntity<Rule>> updateRule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Rule rule
    ) throws URISyntaxException {
        log.debug("REST request to update Rule : {}, {}", id, rule);
        if (rule.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rule.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ruleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ruleRepository
                    .save(rule)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /rules/:id} : Partial updates given fields of an existing rule, field will ignore if it is null
     *
     * @param id the id of the rule to save.
     * @param rule the rule to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rule,
     * or with status {@code 400 (Bad Request)} if the rule is not valid,
     * or with status {@code 404 (Not Found)} if the rule is not found,
     * or with status {@code 500 (Internal Server Error)} if the rule couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rules/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Rule>> partialUpdateRule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Rule rule
    ) throws URISyntaxException {
        log.debug("REST request to partial update Rule partially : {}, {}", id, rule);
        if (rule.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rule.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ruleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Rule> result = ruleRepository
                    .findById(rule.getId())
                    .map(existingRule -> {
                        if (rule.getName() != null) {
                            existingRule.setName(rule.getName());
                        }
                        if (rule.getRaw() != null) {
                            existingRule.setRaw(rule.getRaw());
                        }

                        return existingRule;
                    })
                    .flatMap(ruleRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /rules} : get all the rules.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rules in body.
     */
    @GetMapping("/rules")
    public Mono<ResponseEntity<List<Rule>>> getAllRules(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Rules");
        return ruleRepository
            .count()
            .zipWith(ruleRepository.findAllBy(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /rules/:id} : get the "id" rule.
     *
     * @param id the id of the rule to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rule, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rules/{id}")
    public Mono<ResponseEntity<Rule>> getRule(@PathVariable Long id) {
        log.debug("REST request to get Rule : {}", id);
        Mono<Rule> rule = ruleRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(rule);
    }

    /**
     * {@code DELETE  /rules/:id} : delete the "id" rule.
     *
     * @param id the id of the rule to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rules/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRule(@PathVariable Long id) {
        log.debug("REST request to delete Rule : {}", id);
        return ruleRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}

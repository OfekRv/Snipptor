package snipptor.snipptor.snipptor.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.repository.SnippetMatchedRulesRepository;
import snipptor.snipptor.snipptor.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link snipptor.snipptor.snipptor.domain.SnippetMatchedRules}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SnippetMatchedRulesResource {

    private final Logger log = LoggerFactory.getLogger(SnippetMatchedRulesResource.class);

    private static final String ENTITY_NAME = "snippetMatchedRules";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SnippetMatchedRulesRepository snippetMatchedRulesRepository;

    public SnippetMatchedRulesResource(SnippetMatchedRulesRepository snippetMatchedRulesRepository) {
        this.snippetMatchedRulesRepository = snippetMatchedRulesRepository;
    }

    /**
     * {@code POST  /snippet-matched-rules} : Create a new snippetMatchedRules.
     *
     * @param snippetMatchedRules the snippetMatchedRules to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new snippetMatchedRules, or with status {@code 400 (Bad Request)} if the snippetMatchedRules has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/snippet-matched-rules")
    public Mono<ResponseEntity<SnippetMatchedRules>> createSnippetMatchedRules(@RequestBody SnippetMatchedRules snippetMatchedRules)
        throws URISyntaxException {
        log.debug("REST request to save SnippetMatchedRules : {}", snippetMatchedRules);
        if (snippetMatchedRules.getId() != null) {
            throw new BadRequestAlertException("A new snippetMatchedRules cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return snippetMatchedRulesRepository
            .save(snippetMatchedRules)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/snippet-matched-rules/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /snippet-matched-rules/:id} : Updates an existing snippetMatchedRules.
     *
     * @param id the id of the snippetMatchedRules to save.
     * @param snippetMatchedRules the snippetMatchedRules to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated snippetMatchedRules,
     * or with status {@code 400 (Bad Request)} if the snippetMatchedRules is not valid,
     * or with status {@code 500 (Internal Server Error)} if the snippetMatchedRules couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/snippet-matched-rules/{id}")
    public Mono<ResponseEntity<SnippetMatchedRules>> updateSnippetMatchedRules(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SnippetMatchedRules snippetMatchedRules
    ) throws URISyntaxException {
        log.debug("REST request to update SnippetMatchedRules : {}, {}", id, snippetMatchedRules);
        if (snippetMatchedRules.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, snippetMatchedRules.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return snippetMatchedRulesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return snippetMatchedRulesRepository
                    .save(snippetMatchedRules)
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
     * {@code PATCH  /snippet-matched-rules/:id} : Partial updates given fields of an existing snippetMatchedRules, field will ignore if it is null
     *
     * @param id the id of the snippetMatchedRules to save.
     * @param snippetMatchedRules the snippetMatchedRules to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated snippetMatchedRules,
     * or with status {@code 400 (Bad Request)} if the snippetMatchedRules is not valid,
     * or with status {@code 404 (Not Found)} if the snippetMatchedRules is not found,
     * or with status {@code 500 (Internal Server Error)} if the snippetMatchedRules couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/snippet-matched-rules/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SnippetMatchedRules>> partialUpdateSnippetMatchedRules(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SnippetMatchedRules snippetMatchedRules
    ) throws URISyntaxException {
        log.debug("REST request to partial update SnippetMatchedRules partially : {}, {}", id, snippetMatchedRules);
        if (snippetMatchedRules.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, snippetMatchedRules.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return snippetMatchedRulesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SnippetMatchedRules> result = snippetMatchedRulesRepository
                    .findById(snippetMatchedRules.getId())
                    .map(existingSnippetMatchedRules -> {
                        return existingSnippetMatchedRules;
                    })
                    .flatMap(snippetMatchedRulesRepository::save);

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
     * {@code GET  /snippet-matched-rules} : get all the snippetMatchedRules.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of snippetMatchedRules in body.
     */
    @GetMapping("/snippet-matched-rules")
    public Mono<List<SnippetMatchedRules>> getAllSnippetMatchedRules() {
        log.debug("REST request to get all SnippetMatchedRules");
        return snippetMatchedRulesRepository.findAll().collectList();
    }

    /**
     * {@code GET  /snippet-matched-rules} : get all the snippetMatchedRules as a stream.
     * @return the {@link Flux} of snippetMatchedRules.
     */
    @GetMapping(value = "/snippet-matched-rules", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SnippetMatchedRules> getAllSnippetMatchedRulesAsStream() {
        log.debug("REST request to get all SnippetMatchedRules as a stream");
        return snippetMatchedRulesRepository.findAll();
    }

    /**
     * {@code GET  /snippet-matched-rules/:id} : get the "id" snippetMatchedRules.
     *
     * @param id the id of the snippetMatchedRules to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the snippetMatchedRules, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/snippet-matched-rules/{id}")
    public Mono<ResponseEntity<SnippetMatchedRules>> getSnippetMatchedRules(@PathVariable Long id) {
        log.debug("REST request to get SnippetMatchedRules : {}", id);
        Mono<SnippetMatchedRules> snippetMatchedRules = snippetMatchedRulesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(snippetMatchedRules);
    }

    /**
     * {@code DELETE  /snippet-matched-rules/:id} : delete the "id" snippetMatchedRules.
     *
     * @param id the id of the snippetMatchedRules to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/snippet-matched-rules/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSnippetMatchedRules(@PathVariable Long id) {
        log.debug("REST request to delete SnippetMatchedRules : {}", id);
        return snippetMatchedRulesRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}

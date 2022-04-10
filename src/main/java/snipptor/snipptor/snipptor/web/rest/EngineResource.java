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
import snipptor.snipptor.snipptor.domain.Engine;
import snipptor.snipptor.snipptor.repository.EngineRepository;
import snipptor.snipptor.snipptor.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link snipptor.snipptor.snipptor.domain.Engine}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EngineResource {

    private final Logger log = LoggerFactory.getLogger(EngineResource.class);

    private static final String ENTITY_NAME = "engine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EngineRepository engineRepository;

    public EngineResource(EngineRepository engineRepository) {
        this.engineRepository = engineRepository;
    }

    /**
     * {@code POST  /engines} : Create a new engine.
     *
     * @param engine the engine to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new engine, or with status {@code 400 (Bad Request)} if the engine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/engines")
    public Mono<ResponseEntity<Engine>> createEngine(@Valid @RequestBody Engine engine) throws URISyntaxException {
        log.debug("REST request to save Engine : {}", engine);
        if (engine.getId() != null) {
            throw new BadRequestAlertException("A new engine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return engineRepository
            .save(engine)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/engines/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /engines/:id} : Updates an existing engine.
     *
     * @param id the id of the engine to save.
     * @param engine the engine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engine,
     * or with status {@code 400 (Bad Request)} if the engine is not valid,
     * or with status {@code 500 (Internal Server Error)} if the engine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/engines/{id}")
    public Mono<ResponseEntity<Engine>> updateEngine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Engine engine
    ) throws URISyntaxException {
        log.debug("REST request to update Engine : {}, {}", id, engine);
        if (engine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return engineRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return engineRepository
                    .save(engine)
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
     * {@code PATCH  /engines/:id} : Partial updates given fields of an existing engine, field will ignore if it is null
     *
     * @param id the id of the engine to save.
     * @param engine the engine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engine,
     * or with status {@code 400 (Bad Request)} if the engine is not valid,
     * or with status {@code 404 (Not Found)} if the engine is not found,
     * or with status {@code 500 (Internal Server Error)} if the engine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/engines/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Engine>> partialUpdateEngine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Engine engine
    ) throws URISyntaxException {
        log.debug("REST request to partial update Engine partially : {}, {}", id, engine);
        if (engine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return engineRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Engine> result = engineRepository
                    .findById(engine.getId())
                    .map(existingEngine -> {
                        if (engine.getName() != null) {
                            existingEngine.setName(engine.getName());
                        }
                        if (engine.getUrl() != null) {
                            existingEngine.setUrl(engine.getUrl());
                        }

                        return existingEngine;
                    })
                    .flatMap(engineRepository::save);

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
     * {@code GET  /engines} : get all the engines.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of engines in body.
     */
    @GetMapping("/engines")
    public Mono<ResponseEntity<List<Engine>>> getAllEngines(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Engines");
        return engineRepository
            .count()
            .zipWith(engineRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /engines/:id} : get the "id" engine.
     *
     * @param id the id of the engine to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the engine, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/engines/{id}")
    public Mono<ResponseEntity<Engine>> getEngine(@PathVariable Long id) {
        log.debug("REST request to get Engine : {}", id);
        Mono<Engine> engine = engineRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(engine);
    }

    /**
     * {@code DELETE  /engines/:id} : delete the "id" engine.
     *
     * @param id the id of the engine to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/engines/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteEngine(@PathVariable Long id) {
        log.debug("REST request to delete Engine : {}", id);
        return engineRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}

package snipptor.snipptor.snipptor.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.shaded.com.google.common.hash.Hashing;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.Snippet;
import snipptor.snipptor.snipptor.repository.SnippetRepository;
import snipptor.snipptor.snipptor.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link snipptor.snipptor.snipptor.domain.Snippet}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SnippetResource {

    private final Logger log = LoggerFactory.getLogger(SnippetResource.class);

    private static final String ENTITY_NAME = "snippet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SnippetRepository snippetRepository;

    public SnippetResource(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    /**
     * {@code POST  /snippets} : Create a new snippet.
     *
     * @param snippet the snippet to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new snippet, or with status {@code 400 (Bad Request)} if the snippet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/snippets")
    public Mono<ResponseEntity<Snippet>> createSnippet(@Valid @RequestBody Snippet snippet) throws URISyntaxException {
        log.debug("REST request to save Snippet : {}", snippet);
        if (snippet.getId() != null) {
            throw new BadRequestAlertException("A new snippet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        snippet.setHash(createHashFromContent(snippet.getContent()));

        return snippetRepository
            .save(snippet)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/snippets/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    private String createHashFromContent(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                content.trim().replaceAll(" +", " ").getBytes(StandardCharsets.UTF_8));

            return new String(Hex.encode(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * {@code PUT  /snippets/:id} : Updates an existing snippet.
     *
     * @param id      the id of the snippet to save.
     * @param snippet the snippet to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated snippet,
     * or with status {@code 400 (Bad Request)} if the snippet is not valid,
     * or with status {@code 500 (Internal Server Error)} if the snippet couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/snippets/{id}")
    public Mono<ResponseEntity<Snippet>> updateSnippet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Snippet snippet
    ) throws URISyntaxException {
        log.debug("REST request to update Snippet : {}, {}", id, snippet);
        if (snippet.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, snippet.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return snippetRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return snippetRepository
                    .save(snippet)
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
     * {@code PATCH  /snippets/:id} : Partial updates given fields of an existing snippet, field will ignore if it is null
     *
     * @param id      the id of the snippet to save.
     * @param snippet the snippet to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated snippet,
     * or with status {@code 400 (Bad Request)} if the snippet is not valid,
     * or with status {@code 404 (Not Found)} if the snippet is not found,
     * or with status {@code 500 (Internal Server Error)} if the snippet couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/snippets/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public Mono<ResponseEntity<Snippet>> partialUpdateSnippet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Snippet snippet
    ) throws URISyntaxException {
        log.debug("REST request to partial update Snippet partially : {}, {}", id, snippet);
        if (snippet.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, snippet.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return snippetRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Snippet> result = snippetRepository
                    .findById(snippet.getId())
                    .map(existingSnippet -> {
                        if (snippet.getHash() != null) {
                            existingSnippet.setHash(snippet.getHash());
                        }
                        if (snippet.getContent() != null) {
                            existingSnippet.setContent(snippet.getContent());
                        }
                        if (snippet.getUrl() != null) {
                            existingSnippet.setUrl(snippet.getUrl());
                        }
                        if (snippet.getClassification() != null) {
                            existingSnippet.setClassification(snippet.getClassification());
                        }
                        if (snippet.getScanCount() != null) {
                            existingSnippet.setScanCount(snippet.getScanCount());
                        }

                        return existingSnippet;
                    })
                    .flatMap(snippetRepository::save);

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
     * {@code GET  /snippets} : get all the snippets.
     *
     * @param pageable  the pagination information.
     * @param request   a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of snippets in body.
     */
    @GetMapping("/snippets")
    public Mono<ResponseEntity<List<Snippet>>> getAllSnippets(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Snippets");
        return snippetRepository
            .count()
            .zipWith(snippetRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /snippets/:id} : get the "id" snippet.
     *
     * @param id the id of the snippet to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the snippet, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/snippets/{id}")
    public Mono<ResponseEntity<Snippet>> getSnippet(@PathVariable Long id) {
        log.debug("REST request to get Snippet : {}", id);
        Mono<Snippet> snippet = snippetRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(snippet);
    }

    /**
     * {@code DELETE  /snippets/:id} : delete the "id" snippet.
     *
     * @param id the id of the snippet to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/snippets/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSnippet(@PathVariable Long id) {
        log.debug("REST request to delete Snippet : {}", id);
        return snippetRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}

package snipptor.snipptor.snipptor.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.repository.SnippetMatchedRulesRepository;
import snipptor.snipptor.snipptor.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link snipptor.snipptor.snipptor.domain.SnippetMatchedRules}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SnippetMatchedRulesResource {

    private final Logger log = LoggerFactory.getLogger(SnippetMatchedRulesResource.class);

    private final SnippetMatchedRulesRepository snippetMatchedRulesRepository;

    public SnippetMatchedRulesResource(SnippetMatchedRulesRepository snippetMatchedRulesRepository) {
        this.snippetMatchedRulesRepository = snippetMatchedRulesRepository;
    }

    /**
     * {@code GET  /snippet-matched-rules} : get all the snippetMatchedRules.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of snippetMatchedRules in body.
     */
    @GetMapping("/snippet-matched-rules")
    public Mono<ResponseEntity<List<SnippetMatchedRules>>> getAllSnippetMatchedRules(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of SnippetMatchedRules");
        return snippetMatchedRulesRepository
            .count()
            .zipWith(snippetMatchedRulesRepository.findAllBy(pageable).collectList())
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
}

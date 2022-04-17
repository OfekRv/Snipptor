package snipptor.snipptor.snipptor.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import snipptor.snipptor.snipptor.domain.Snippet;
import snipptor.snipptor.snipptor.repository.SnippetRepository;
import snipptor.snipptor.snipptor.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

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
    public ResponseEntity<Snippet> createSnippet(@Valid @RequestBody Snippet snippet) throws URISyntaxException {
        log.debug("REST request to save Snippet : {}", snippet);
        if (snippet.getId() != null) {
            throw new BadRequestAlertException("A new snippet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Snippet result = snippetRepository.save(snippet);
        return ResponseEntity
            .created(new URI("/api/snippets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /snippets/:id} : Updates an existing snippet.
     *
     * @param id the id of the snippet to save.
     * @param snippet the snippet to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated snippet,
     * or with status {@code 400 (Bad Request)} if the snippet is not valid,
     * or with status {@code 500 (Internal Server Error)} if the snippet couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/snippets/{id}")
    public ResponseEntity<Snippet> updateSnippet(
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

        if (!snippetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Snippet result = snippetRepository.save(snippet);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, snippet.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /snippets/:id} : Partial updates given fields of an existing snippet, field will ignore if it is null
     *
     * @param id the id of the snippet to save.
     * @param snippet the snippet to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated snippet,
     * or with status {@code 400 (Bad Request)} if the snippet is not valid,
     * or with status {@code 404 (Not Found)} if the snippet is not found,
     * or with status {@code 500 (Internal Server Error)} if the snippet couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/snippets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Snippet> partialUpdateSnippet(
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

        if (!snippetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Snippet> result = snippetRepository
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
            .map(snippetRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, snippet.getId().toString())
        );
    }

    /**
     * {@code GET  /snippets} : get all the snippets.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of snippets in body.
     */
    @GetMapping("/snippets")
    public ResponseEntity<List<Snippet>> getAllSnippets(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Snippets");
        Page<Snippet> page;
        if (eagerload) {
            page = snippetRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = snippetRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /snippets/:id} : get the "id" snippet.
     *
     * @param id the id of the snippet to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the snippet, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/snippets/{id}")
    public ResponseEntity<Snippet> getSnippet(@PathVariable Long id) {
        log.debug("REST request to get Snippet : {}", id);
        Optional<Snippet> snippet = snippetRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(snippet);
    }

    /**
     * {@code DELETE  /snippets/:id} : delete the "id" snippet.
     *
     * @param id the id of the snippet to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/snippets/{id}")
    public ResponseEntity<Void> deleteSnippet(@PathVariable Long id) {
        log.debug("REST request to delete Snippet : {}", id);
        snippetRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

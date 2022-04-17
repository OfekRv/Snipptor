package snipptor.snipptor.snipptor.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.repository.SnippetMatchedRulesRepository;
import snipptor.snipptor.snipptor.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

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
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of snippetMatchedRules in body.
     */
    @GetMapping("/snippet-matched-rules")
    public ResponseEntity<List<SnippetMatchedRules>> getAllSnippetMatchedRules(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of SnippetMatchedRules");
        Page<SnippetMatchedRules> page = snippetMatchedRulesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /snippet-matched-rules/:id} : get the "id" snippetMatchedRules.
     *
     * @param id the id of the snippetMatchedRules to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the snippetMatchedRules, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/snippet-matched-rules/{id}")
    public ResponseEntity<SnippetMatchedRules> getSnippetMatchedRules(@PathVariable Long id) {
        log.debug("REST request to get SnippetMatchedRules : {}", id);
        Optional<SnippetMatchedRules> snippetMatchedRules = snippetMatchedRulesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(snippetMatchedRules);
    }
}

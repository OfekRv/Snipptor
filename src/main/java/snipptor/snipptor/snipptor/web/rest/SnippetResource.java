package snipptor.snipptor.snipptor.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import snipptor.snipptor.snipptor.bl.ScannerBl;
import snipptor.snipptor.snipptor.domain.Rule;
import snipptor.snipptor.snipptor.domain.Snippet;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.repository.EngineRepository;
import snipptor.snipptor.snipptor.repository.RuleRepository;
import snipptor.snipptor.snipptor.repository.SnippetMatchedRulesRepository;
import snipptor.snipptor.snipptor.repository.SnippetRepository;
import snipptor.snipptor.snipptor.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static snipptor.snipptor.snipptor.domain.enumeration.SnippetClassification.classifyByRules;

/**
 * REST controller for managing {@link snipptor.snipptor.snipptor.domain.Snippet}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SnippetResource {
    public static final long FIRST_SCAN = 1l;
    private final Logger log = LoggerFactory.getLogger(SnippetResource.class);

    private static final String ENTITY_NAME = "snippet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private RestTemplate restClient;

    private final ScannerBl scannerBl;
    private final SnippetRepository snippetRepository;
    private final RuleRepository ruleRepository;
    private final EngineRepository engineRepository;
    private final SnippetMatchedRulesRepository MatchedRulesRepository;

    public SnippetResource(ScannerBl scannerBl, SnippetRepository snippetRepository, RuleRepository ruleRepository, EngineRepository engineRepository, SnippetMatchedRulesRepository matchedRulesRepository) {
        this.scannerBl = scannerBl;
        this.snippetRepository = snippetRepository;
        this.ruleRepository = ruleRepository;
        this.engineRepository = engineRepository;
        this.MatchedRulesRepository = matchedRulesRepository;

        restClient = new RestTemplate();
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

        SnippetMatchedRules matched;
        String snippetHash = calculateHashFromContent(snippet.getContent());
        Optional<Snippet> alreadyExistSnippet = snippetRepository.findByHash(snippetHash);
        if (alreadyExistSnippet.isPresent()) {
            snippet = alreadyExistSnippet.get();
            snippet.setScanCount(snippet.getScanCount() + 1);
        } else {
            snippet.setHash(snippetHash);
            snippet.setScanCount(FIRST_SCAN);
            matched = new SnippetMatchedRules();

            String content = snippet.getContent();
            Set<Rule> matchedRules = engineRepository.findAll().stream()
                .map(e -> scannerBl.scanSnippet(e, content))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
            matched.setRules(matchedRules);
            snippet.setMatchedRules(matched);
            snippet.setClassification(classifyByRules(matchedRules));
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
     * @param id      the id of the snippet to save.
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

        Snippet result = snippetRepository.findById(id).get();
        result.setClassification(snippet.getClassification());
        result = snippetRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, snippet.getId().toString()))
            .body(result);
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
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of snippets in body.
     */
    @GetMapping("/snippets")
    public ResponseEntity<List<Snippet>> getAllSnippets(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Snippets");
        Page<Snippet> page = snippetRepository.findAll(pageable);
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
        Optional<Snippet> snippet = snippetRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(snippet);
    }

    /**
     * {@code POST  /snippets/actions/retro-scan} : Scans all the snippets with all the current rules.
     */
    @PostMapping("/snippets/actions/retro-scan")
    public ResponseEntity<Void> retroScan() throws InterruptedException {
        scannerBl.retroScanSnippets();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private String calculateHashFromContent(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                content.trim().replaceAll(" +", " ").getBytes(StandardCharsets.UTF_8));

            return new String(Hex.encode(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

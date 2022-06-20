package snipptor.snipptor.snipptor.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import snipptor.snipptor.snipptor.contracts.ScanSnippet;
import snipptor.snipptor.snipptor.contracts.SnippetRulesScanResult;
import snipptor.snipptor.snipptor.domain.Engine;
import snipptor.snipptor.snipptor.domain.Rule;
import snipptor.snipptor.snipptor.domain.Snippet;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.repository.EngineRepository;
import snipptor.snipptor.snipptor.repository.RuleRepository;
import snipptor.snipptor.snipptor.repository.SnippetMatchedRulesRepository;
import snipptor.snipptor.snipptor.repository.SnippetRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static snipptor.snipptor.snipptor.domain.enumeration.SnippetClassification.*;

@Component
@Transactional
public class ScannerBl {
    private final Logger log = LoggerFactory.getLogger(ScannerBl.class);

    private RestTemplate restClient;

    private final SnippetRepository snippetRepository;
    private final RuleRepository ruleRepository;
    private final EngineRepository engineRepository;
    private final SnippetMatchedRulesRepository MatchedRulesRepository;

    public ScannerBl(SnippetRepository snippetRepository, RuleRepository ruleRepository, EngineRepository engineRepository, SnippetMatchedRulesRepository matchedRulesRepository) {
        this.snippetRepository = snippetRepository;
        this.ruleRepository = ruleRepository;
        this.engineRepository = engineRepository;
        MatchedRulesRepository = matchedRulesRepository;

        restClient = new RestTemplate();
    }

    @Async
    public void retroScanSnippets() {
        log.info("Retro scan of all snippets with all rules started");

        Collection<Engine> engines = engineRepository.findAll();
        snippetRepository.findAll().forEach(snippet -> {
            log.info("Retro scan of snippet:" + snippet.getId() + " has been started");
            updateSnippetScan(snippet, engines);
            if (needToReClassify(snippet)) {
                snippet.setClassification(classifyByRules(snippet.getMatchedRules().getRules()));
            }
            snippetRepository.save(snippet);
            log.info("Retro scan of snippet:" + snippet.getId() + " finished");
        });
        log.info("Retro scan of all snippets with all rules finished");
    }

    @Async
    public Snippet updateSnippetScan(Snippet snippet, Collection<Engine> engines) {
        SnippetMatchedRules matched = Optional.ofNullable(snippet.getMatchedRules())
            .orElse(new SnippetMatchedRules());
        Set<Rule> matchedRules = engines.stream()
            .map(e -> scanSnippet(e, snippet.getContent()))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        matched.setRules(matchedRules);
        snippet.setMatchedRules(matched);
        return snippet;
    }

    public Collection<Rule> scanSnippet(Engine engine, String rawSnippet) {
        SnippetRulesScanResult result =
            restClient.postForObject(engine.getUrl() + "/scan",
                new ScanSnippet(rawSnippet),
                SnippetRulesScanResult.class);
        return result.getMatches().stream()
            .map(r -> ruleRepository.findByName(r))
            .filter(r -> r.isPresent())
            .map(r -> r.get())
            .collect(Collectors.toSet());
    }

    private boolean needToReClassify(Snippet snippet) {
        // those are manually submitted and highly trusted
        return !List.of(SAFE, MALICIOUS).contains(snippet.getClassification());
    }
}

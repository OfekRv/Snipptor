package snipptor.snipptor.snipptor.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.Rule;

/**
 * Spring Data SQL reactive repository for the Rule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RuleRepository extends ReactiveCrudRepository<Rule, Long>, RuleRepositoryInternal {
    Flux<Rule> findAllBy(Pageable pageable);

    @Override
    Mono<Rule> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Rule> findAllWithEagerRelationships();

    @Override
    Flux<Rule> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM rule entity WHERE entity.engine_id = :id")
    Flux<Rule> findByEngine(Long id);

    @Query("SELECT * FROM rule entity WHERE entity.engine_id IS NULL")
    Flux<Rule> findAllWhereEngineIsNull();

    @Query("SELECT * FROM rule entity WHERE entity.vulnerability_id = :id")
    Flux<Rule> findByVulnerability(Long id);

    @Query("SELECT * FROM rule entity WHERE entity.vulnerability_id IS NULL")
    Flux<Rule> findAllWhereVulnerabilityIsNull();

    @Query(
        "SELECT entity.* FROM rule entity JOIN rel_rule__snippet_matched_rules joinTable ON entity.id = joinTable.rule_id WHERE joinTable.snippet_matched_rules_id = :id"
    )
    Flux<Rule> findBySnippetMatchedRules(Long id);

    @Override
    <S extends Rule> Mono<S> save(S entity);

    @Override
    Flux<Rule> findAll();

    @Override
    Mono<Rule> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RuleRepositoryInternal {
    <S extends Rule> Mono<S> save(S entity);

    Flux<Rule> findAllBy(Pageable pageable);

    Flux<Rule> findAll();

    Mono<Rule> findById(Long id);

    Flux<Rule> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Rule> findOneWithEagerRelationships(Long id);

    Flux<Rule> findAllWithEagerRelationships();

    Flux<Rule> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}

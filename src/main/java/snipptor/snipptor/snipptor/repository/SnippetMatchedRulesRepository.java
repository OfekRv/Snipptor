package snipptor.snipptor.snipptor.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;

/**
 * Spring Data SQL reactive repository for the SnippetMatchedRules entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SnippetMatchedRulesRepository
    extends ReactiveCrudRepository<SnippetMatchedRules, Long>, SnippetMatchedRulesRepositoryInternal {
    @Override
    <S extends SnippetMatchedRules> Mono<S> save(S entity);

    @Override
    Flux<SnippetMatchedRules> findAll();

    @Override
    Mono<SnippetMatchedRules> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SnippetMatchedRulesRepositoryInternal {
    <S extends SnippetMatchedRules> Mono<S> save(S entity);

    Flux<SnippetMatchedRules> findAllBy(Pageable pageable);

    Flux<SnippetMatchedRules> findAll();

    Mono<SnippetMatchedRules> findById(Long id);

    Flux<SnippetMatchedRules> findAllBy(Pageable pageable, Criteria criteria);
}

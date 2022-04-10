package snipptor.snipptor.snipptor.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.Snippet;

/**
 * Spring Data SQL reactive repository for the Snippet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SnippetRepository extends ReactiveCrudRepository<Snippet, Long>, SnippetRepositoryInternal {
    Flux<Snippet> findAllBy(Pageable pageable);

    @Override
    Mono<Snippet> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Snippet> findAllWithEagerRelationships();

    @Override
    Flux<Snippet> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM snippet entity JOIN rel_snippet__snippet_matched_rules joinTable ON entity.id = joinTable.snippet_id WHERE joinTable.snippet_matched_rules_id = :id"
    )
    Flux<Snippet> findBySnippetMatchedRules(Long id);

    @Override
    <S extends Snippet> Mono<S> save(S entity);

    @Override
    Flux<Snippet> findAll();

    @Override
    Mono<Snippet> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SnippetRepositoryInternal {
    <S extends Snippet> Mono<S> save(S entity);

    Flux<Snippet> findAllBy(Pageable pageable);

    Flux<Snippet> findAll();

    Mono<Snippet> findById(Long id);

    Flux<Snippet> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Snippet> findOneWithEagerRelationships(Long id);

    Flux<Snippet> findAllWithEagerRelationships();

    Flux<Snippet> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}

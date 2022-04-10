package snipptor.snipptor.snipptor.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.Engine;

/**
 * Spring Data SQL reactive repository for the Engine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EngineRepository extends ReactiveCrudRepository<Engine, Long>, EngineRepositoryInternal {
    @Override
    <S extends Engine> Mono<S> save(S entity);

    @Override
    Flux<Engine> findAll();

    @Override
    Mono<Engine> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EngineRepositoryInternal {
    <S extends Engine> Mono<S> save(S entity);

    Flux<Engine> findAllBy(Pageable pageable);

    Flux<Engine> findAll();

    Mono<Engine> findById(Long id);

    Flux<Engine> findAllBy(Pageable pageable, Criteria criteria);
}

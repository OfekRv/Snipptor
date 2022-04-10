package snipptor.snipptor.snipptor.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.Snippet;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.domain.enumeration.SnippetClassification;
import snipptor.snipptor.snipptor.repository.rowmapper.SnippetRowMapper;

/**
 * Spring Data SQL reactive custom repository implementation for the Snippet entity.
 */
@SuppressWarnings("unused")
class SnippetRepositoryInternalImpl extends SimpleR2dbcRepository<Snippet, Long> implements SnippetRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SnippetRowMapper snippetMapper;

    private static final Table entityTable = Table.aliased("snippet", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable snippetMatchedRulesLink = new EntityManager.LinkTable(
        "rel_snippet__snippet_matched_rules",
        "snippet_id",
        "snippet_matched_rules_id"
    );

    public SnippetRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SnippetRowMapper snippetMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Snippet.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.snippetMapper = snippetMapper;
    }

    @Override
    public Flux<Snippet> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Snippet> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Snippet> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = SnippetSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Snippet.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Snippet> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Snippet> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    @Override
    public Mono<Snippet> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Snippet> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Snippet> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Snippet process(Row row, RowMetadata metadata) {
        Snippet entity = snippetMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Snippet> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Snippet> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(
                snippetMatchedRulesLink,
                entity.getId(),
                entity.getSnippetMatchedRules().stream().map(SnippetMatchedRules::getId)
            )
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(snippetMatchedRulesLink, entityId);
    }
}

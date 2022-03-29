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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import snipptor.snipptor.snipptor.domain.Rule;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;
import snipptor.snipptor.snipptor.repository.rowmapper.EngineRowMapper;
import snipptor.snipptor.snipptor.repository.rowmapper.RuleRowMapper;
import snipptor.snipptor.snipptor.repository.rowmapper.VulnerabilityRowMapper;

/**
 * Spring Data SQL reactive custom repository implementation for the Rule entity.
 */
@SuppressWarnings("unused")
class RuleRepositoryInternalImpl extends SimpleR2dbcRepository<Rule, Long> implements RuleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EngineRowMapper engineMapper;
    private final VulnerabilityRowMapper vulnerabilityMapper;
    private final RuleRowMapper ruleMapper;

    private static final Table entityTable = Table.aliased("rule", EntityManager.ENTITY_ALIAS);
    private static final Table engineTable = Table.aliased("engine", "engine");
    private static final Table vulnerabilityTable = Table.aliased("vulnerability", "vulnerability");

    private static final EntityManager.LinkTable snippetMatchedRulesLink = new EntityManager.LinkTable(
        "rel_rule__snippet_matched_rules",
        "rule_id",
        "snippet_matched_rules_id"
    );

    public RuleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EngineRowMapper engineMapper,
        VulnerabilityRowMapper vulnerabilityMapper,
        RuleRowMapper ruleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Rule.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.engineMapper = engineMapper;
        this.vulnerabilityMapper = vulnerabilityMapper;
        this.ruleMapper = ruleMapper;
    }

    @Override
    public Flux<Rule> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Rule> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Rule> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RuleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EngineSqlHelper.getColumns(engineTable, "engine"));
        columns.addAll(VulnerabilitySqlHelper.getColumns(vulnerabilityTable, "vulnerability"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(engineTable)
            .on(Column.create("engine_id", entityTable))
            .equals(Column.create("id", engineTable))
            .leftOuterJoin(vulnerabilityTable)
            .on(Column.create("vulnerability_id", entityTable))
            .equals(Column.create("id", vulnerabilityTable));

        String select = entityManager.createSelect(selectFrom, Rule.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Rule> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Rule> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    @Override
    public Mono<Rule> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Rule> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Rule> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Rule process(Row row, RowMetadata metadata) {
        Rule entity = ruleMapper.apply(row, "e");
        entity.setEngine(engineMapper.apply(row, "engine"));
        entity.setVulnerability(vulnerabilityMapper.apply(row, "vulnerability"));
        return entity;
    }

    @Override
    public <S extends Rule> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Rule> Mono<S> updateRelations(S entity) {
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

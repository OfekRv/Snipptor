package snipptor.snipptor.snipptor.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import snipptor.snipptor.snipptor.domain.Rule;

/**
 * Converter between {@link Row} to {@link Rule}, with proper type conversions.
 */
@Service
public class RuleRowMapper implements BiFunction<Row, String, Rule> {

    private final ColumnConverter converter;

    public RuleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Rule} stored in the database.
     */
    @Override
    public Rule apply(Row row, String prefix) {
        Rule entity = new Rule();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setRaw(converter.fromRow(row, prefix + "_raw", String.class));
        entity.setEngineId(converter.fromRow(row, prefix + "_engine_id", Long.class));
        entity.setVulnerabilityId(converter.fromRow(row, prefix + "_vulnerability_id", Long.class));
        return entity;
    }
}

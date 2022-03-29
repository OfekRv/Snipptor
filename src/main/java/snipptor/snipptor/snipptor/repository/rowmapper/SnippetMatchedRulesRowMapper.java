package snipptor.snipptor.snipptor.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;

/**
 * Converter between {@link Row} to {@link SnippetMatchedRules}, with proper type conversions.
 */
@Service
public class SnippetMatchedRulesRowMapper implements BiFunction<Row, String, SnippetMatchedRules> {

    private final ColumnConverter converter;

    public SnippetMatchedRulesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link SnippetMatchedRules} stored in the database.
     */
    @Override
    public SnippetMatchedRules apply(Row row, String prefix) {
        SnippetMatchedRules entity = new SnippetMatchedRules();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        return entity;
    }
}

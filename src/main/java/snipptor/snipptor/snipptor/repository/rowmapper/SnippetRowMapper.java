package snipptor.snipptor.snipptor.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import snipptor.snipptor.snipptor.domain.Snippet;

/**
 * Converter between {@link Row} to {@link Snippet}, with proper type conversions.
 */
@Service
public class SnippetRowMapper implements BiFunction<Row, String, Snippet> {

    private final ColumnConverter converter;

    public SnippetRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Snippet} stored in the database.
     */
    @Override
    public Snippet apply(Row row, String prefix) {
        Snippet entity = new Snippet();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setHash(converter.fromRow(row, prefix + "_hash", String.class));
        entity.setContent(converter.fromRow(row, prefix + "_content", String.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        return entity;
    }
}

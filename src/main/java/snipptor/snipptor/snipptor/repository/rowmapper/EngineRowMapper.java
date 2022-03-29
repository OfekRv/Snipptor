package snipptor.snipptor.snipptor.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import snipptor.snipptor.snipptor.domain.Engine;

/**
 * Converter between {@link Row} to {@link Engine}, with proper type conversions.
 */
@Service
public class EngineRowMapper implements BiFunction<Row, String, Engine> {

    private final ColumnConverter converter;

    public EngineRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Engine} stored in the database.
     */
    @Override
    public Engine apply(Row row, String prefix) {
        Engine entity = new Engine();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        return entity;
    }
}

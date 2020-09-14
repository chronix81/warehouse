package pl.mb.warehouse.domain;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * Value object describing dataset column name.
 */
@Getter
@EqualsAndHashCode
public final class DatasetColumnName {

    @JsonValue
    private final String value;

    public DatasetColumnName(String value) {
        if (Objects.isNull(value) || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Dataset column name name cannot be null or empty.");
        }
        this.value = value;
    }

    public static DatasetColumnName of(String value) {
        return new DatasetColumnName(value);
    }
}

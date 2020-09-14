package pl.mb.warehouse.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object describing data set name.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class DatasetName {

    @JsonValue
    private final String value;

    public DatasetName(String value) {
        DomainAsserts.assertArgumentNotEmpty(value, "Dataset's name must be specified.");
        this.value = value;
    }

    public static DatasetName of(String value) {
        return new DatasetName(value);
    }
}

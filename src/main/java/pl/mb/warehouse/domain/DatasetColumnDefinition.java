package pl.mb.warehouse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Describes single column in a dataset.
 */
@Getter
@ToString
@EqualsAndHashCode
public class DatasetColumnDefinition {

    private final DatasetColumnName name;

    private final DatasetColumnType type;

    public DatasetColumnDefinition(DatasetColumnName name, DatasetColumnType type) {
        DomainAsserts.assertArgumentNotNull(name, "Name must be specified in order to create a column definition.");
        DomainAsserts.assertArgumentNotNull(type, "Type must be specified in order to create a column definition.");
        this.name = name;
        this.type = type;
    }

}

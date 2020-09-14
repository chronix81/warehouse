package pl.mb.warehouse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public abstract class DatasetColumnFilter {

    private final DatasetColumnName columnName;

    DatasetColumnFilter(DatasetColumnName columnName) {
        DomainAsserts.assertArgumentNotNull(columnName, "Column must be specified when creating a filter.");
        this.columnName = columnName;
    }

}

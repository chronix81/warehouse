package pl.mb.warehouse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

@Getter
@EqualsAndHashCode
@ToString
public class SimpleQueryCriteria {
    private final DatasetName datasetName;
    private final Set<DatasetColumnName> requestedColumns;
    private final Set<DatasetColumnFilter> filters;

    SimpleQueryCriteria(DatasetName datasetName, Set<DatasetColumnName> requestedColumns) {
        DomainAsserts.assertArgumentNotNull(datasetName, "Dataset name must be specified.");
        DomainAsserts.assertCollectionNotEmpty(requestedColumns, "At least one column must be selected in query response.");
        this.datasetName = datasetName;
        this.requestedColumns = requestedColumns;
        this.filters = Set.of();
    }

    SimpleQueryCriteria(DatasetName datasetName, Set<DatasetColumnName> requestedColumns, Set<DatasetColumnFilter> filters) {
        DomainAsserts.assertArgumentNotNull(datasetName, "Dataset name must be specified.");
        DomainAsserts.assertCollectionNotEmpty(requestedColumns, "At least one column must be selected in query response.");
        DomainAsserts.assertCollectionNotEmpty(filters, "No filters passed.");
        this.datasetName = datasetName;
        this.requestedColumns = requestedColumns;
        this.filters = Collections.unmodifiableSet(filters);
    }

}

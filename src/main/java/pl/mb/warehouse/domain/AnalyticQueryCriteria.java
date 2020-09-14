package pl.mb.warehouse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Getter
@EqualsAndHashCode
@ToString
public class AnalyticQueryCriteria {
    private final DatasetName datasetName;
    private final DataAggregationType aggregationType;
    private final DatasetColumnName targetColumn;
    private final Optional<VirtualColumnDefinition> virtualColumn;
    private final Set<DatasetColumnFilter> filters;
    private final Set<DatasetColumnName> groupByColumns;

    AnalyticQueryCriteria(DatasetName datasetName, DataAggregationType aggregationType, DatasetColumnName targetColumn,
                          Optional<VirtualColumnDefinition> virtualColumn, Set<DatasetColumnFilter> filters, Set<DatasetColumnName> groupByColumns) {
        this.datasetName = datasetName;
        this.aggregationType = aggregationType;
        this.targetColumn = targetColumn;
        this.virtualColumn = virtualColumn;
        this.filters = Collections.unmodifiableSet(filters);
        this.groupByColumns = Collections.unmodifiableSet(groupByColumns);
    }

}

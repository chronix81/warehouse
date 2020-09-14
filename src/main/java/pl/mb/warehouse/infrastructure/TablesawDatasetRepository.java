package pl.mb.warehouse.infrastructure;

import org.springframework.stereotype.Repository;
import pl.mb.warehouse.domain.AnalyticQueryCriteria;
import pl.mb.warehouse.domain.DataAggregationType;
import pl.mb.warehouse.domain.DataFileSpecification;
import pl.mb.warehouse.domain.Dataset;
import pl.mb.warehouse.domain.DatasetColumnDefinition;
import pl.mb.warehouse.domain.DatasetColumnFilter;
import pl.mb.warehouse.domain.DatasetColumnName;
import pl.mb.warehouse.domain.DatasetColumnType;
import pl.mb.warehouse.domain.DatasetName;
import pl.mb.warehouse.domain.DatasetRepository;
import pl.mb.warehouse.domain.DateRangeFilter;
import pl.mb.warehouse.domain.DateTimeRangeFilter;
import pl.mb.warehouse.domain.DomainAsserts;
import pl.mb.warehouse.domain.DomainException;
import pl.mb.warehouse.domain.QueryResult;
import pl.mb.warehouse.domain.RepositoryException;
import pl.mb.warehouse.domain.SimpleQueryCriteria;
import pl.mb.warehouse.domain.TermFilter;
import pl.mb.warehouse.domain.VirtualColumnDefinition;
import pl.mb.warehouse.domain.VirtualColumnOperationType;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TablesawDatasetRepository implements DatasetRepository {

    private Map<DatasetName, Table> dataSets = new HashMap<>();

    //fixme normally one should care about thread safety more
    @Override
    public Dataset create(DatasetName name, DataFileSpecification fileSpecification) {
        DomainAsserts.assertTrue(!dataSets.containsKey(name), "A dataset with given name already exists.");
        try {
            var csvReadOptions = CsvReadOptions.builderFromUrl(fileSpecification.getLocation().toString())
                    .header(fileSpecification.isHeader())
                    .dateFormat(DateTimeFormatter.ofPattern(fileSpecification.getLocalDateFormat()))
                    .dateTimeFormat(DateTimeFormatter.ofPattern(fileSpecification.getLocalDateTimeFormat()))
                    .separator(fileSpecification.getFieldSeparator())
                    .lineEnding(fileSpecification.getLineEnd())
                    .tableName(name.getValue()).build();

            final var tablesawTable = Table.read().usingOptions(csvReadOptions);
            checkIfAllColumnTypesAreSupported(tablesawTable);
            dataSets.put(name, tablesawTable);
            return toDataset(tablesawTable);
        } catch (IOException e) {
            throw new RepositoryException("Error while creating Dataset. ", e);
        }
    }

    @Override
    public Set<DatasetName> getAllDatasetNames() {
        return dataSets.keySet();
    }

    @Override
    public Optional<Dataset> getByName(DatasetName name) {
        return Optional.ofNullable(dataSets.get(name)).map(this::toDataset);
    }

    @Override
    public Optional<QueryResult> simpleQuery(SimpleQueryCriteria criteria) {
        DomainAsserts.assertTrue(dataSets.containsKey(criteria.getDatasetName()),
                "Requested dataset doesn't exist. Dataset name: " + criteria.getDatasetName().getValue());
        return Optional.ofNullable(dataSets.get(criteria.getDatasetName()))
                .map(table -> applyFilters(table, criteria.getFilters()))
                .map(filteredTable -> filteredTable.select(criteria.getRequestedColumns().stream().map(DatasetColumnName::getValue).toArray(String[]::new)))
                .map(resultTable -> new QueryResult(toStringArray(resultTable)));
    }

    @Override
    public Optional<QueryResult> analyticQuery(AnalyticQueryCriteria criteria) {
        DomainAsserts.assertTrue(dataSets.containsKey(criteria.getDatasetName()),
                "Requested dataset doesn't exist. Dataset name: " + criteria.getDatasetName().getValue());
        return Optional.ofNullable(dataSets.get(criteria.getDatasetName()))
                .map(table -> applyFilters(table, criteria.getFilters()))
                .map(table -> {
                    if (criteria.getVirtualColumn().isPresent()) {
                        return addVirtualColumn(table, criteria.getVirtualColumn().get());
                    }
                    return table;
                })
                .map(table -> table.summarize(criteria.getTargetColumn().getValue(), map(criteria.getAggregationType())))
                .map(summarizer -> criteria.getGroupByColumns().isEmpty() ?
                        summarizer.apply() :
                        summarizer.by(criteria.getGroupByColumns().stream().map(n -> n.getValue()).toArray(String[]::new)))
                .map(summarized -> new QueryResult(toStringArray(summarized)));
    }


    private Table applyFilters(Table sourceTable, Set<DatasetColumnFilter> filters) {
        final var selectionOptional = filters.stream()
                .map(filter -> map(sourceTable, filter))
                .reduce((a, b) -> a.and(b));
        return selectionOptional.isPresent() ? sourceTable.where(selectionOptional.get()) : sourceTable;
    }

    private String[][] toStringArray(Table table) {
        final var result = new String[table.rowCount() + 1][];
        final var header = new String[table.columnNames().size()];
        for (int i = 0; i < table.columnNames().size(); i++) {
            header[i] = table.columnNames().get(i);
        }
        result[0] = header;
        for (int rowIdx = 0; rowIdx < table.rowCount(); rowIdx++) {
            final var row = new String[table.columnNames().size()];
            final var currentRow = table.row(rowIdx);
            for (int i = 0; i < currentRow.columnCount(); i++) {
                row[i] = currentRow.getObject(i).toString();
            }
            result[rowIdx + 1] = row;
        }
        return result;
    }

    private Table addVirtualColumn(Table sourceTable, VirtualColumnDefinition virtualColumnDefinition) {
        //fixme there is a operation type defined so the right operation should be chosen dynamically
        if (!virtualColumnDefinition.getVirtualColumnOperationType().equals(VirtualColumnOperationType.DIVISION)) {
            throw new RuntimeException("Given operation type has not been implemented. " + virtualColumnDefinition.getVirtualColumnOperationType());
        }
        final NumericColumn<?> operand1Column = sourceTable.numberColumn(virtualColumnDefinition.getOperand1().getName().getValue());
        final NumericColumn<?> operand2Column = sourceTable.numberColumn(virtualColumnDefinition.getOperand2().getName().getValue());
        final var results = new Double[sourceTable.rowCount()];
        for (int a = 0; a < sourceTable.rowCount(); a++) {
            results[a] = operand1Column.get(a).doubleValue() / operand2Column.get(a).doubleValue();
        }
        DoubleColumn resultsColumn = DoubleColumn.create(virtualColumnDefinition.getResultingColumn().getName().getValue(), results);
        return sourceTable.addColumns(resultsColumn);
    }

    /**
     * Builds Tablesaw's Selection object based on DatasetColumnFilter.
     *
     * @param table
     * @param filter
     * @return
     */
    private Selection map(Table table, DatasetColumnFilter filter) {
        // waiting for sealed classes in Java
        if (filter instanceof TermFilter) {
            var termFilter = (TermFilter) filter;
            return table.stringColumn(filter.getColumnName().getValue()).isIn(termFilter.getTerms());
        } else if (filter instanceof DateRangeFilter) {
            var dateFilter = (DateRangeFilter) filter;
            return table.dateColumn(dateFilter.getColumnName().getValue()).isBetweenIncluding(dateFilter.getMin(), dateFilter.getMax());
        } else if (filter instanceof DateTimeRangeFilter) {
            var dateTimeFilter = (DateTimeRangeFilter) filter;
            return table.dateTimeColumn(dateTimeFilter.getColumnName().getValue()).isBetweenIncluding(dateTimeFilter.getMin(), dateTimeFilter.getMax());
        }
        throw new DomainException("Unknown filter type. A mapping for given filter has to be implemented.");
    }

    /**
     * Will throw an RepositoryException when unsupported type occurs.
     *
     * @param table
     */
    private void checkIfAllColumnTypesAreSupported(Table table) {
        Arrays.asList(table.columnTypes()).stream()
                .forEach(this::map);
    }

    ;

    private Dataset toDataset(Table table) {
        return new Dataset(
                DatasetName.of(table.name()),
                table.columns().stream()
                        .map(column -> new DatasetColumnDefinition(DatasetColumnName.of(column.name()), map(column.type())))
                        .collect(Collectors.toSet()));
    }

    private DatasetColumnType map(ColumnType type) {
        switch (type.name()) {
            case "LOCAL_DATE":
                return DatasetColumnType.DATE;
            case "LOCAL_DATE_TIME":
                return DatasetColumnType.DATE_TIME;
            case "DOUBLE":
            case "FLOAT":
                return DatasetColumnType.REAL_NUMBER;
            case "INTEGER":
            case "LONG":
            case "SHORT":
                return DatasetColumnType.INTEGER_NUMBER;
            case "STRING":
            case "TEXT":
                return DatasetColumnType.TEXT;
        }
        throw new RepositoryException("Unsupported column type: " + type.name());
    }

    private AggregateFunction map(DataAggregationType aggregationType) {
        switch (aggregationType) {
            case SUM:
                return AggregateFunctions.sum;
            case AVG:
                return AggregateFunctions.mean;
            case MIN:
                return AggregateFunctions.min;
            case MAX:
                return AggregateFunctions.max;
        }
        throw new RepositoryException("Unsupported aggregation type: " + aggregationType.name());
    }

}

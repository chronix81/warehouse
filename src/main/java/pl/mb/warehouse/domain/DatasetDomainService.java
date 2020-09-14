package pl.mb.warehouse.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mb.warehouse.domain.dto.AnalyticQuery;
import pl.mb.warehouse.domain.dto.PlainQuery;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.mb.warehouse.domain.DatasetDomainUtils.findColumnDefinitionByName;

@Service
@RequiredArgsConstructor
public class DatasetDomainService {

    private final DatasetRepository repository;
    private final DomainFilterMapper domainFilterMapper;

    public Dataset createDataset(DatasetName name, DataFileSpecification fileSpecification) {
        DomainAsserts.assertArgumentNotNull(name, "Dataset's name must be specified.");
        DomainAsserts.assertArgumentNotNull(fileSpecification, "Dataset's file specification must be defined.");
        return repository.create(name, fileSpecification);
    }


    public Set<DatasetName> getAllDatasetNames() {
        return repository.getAllDatasetNames();
    }

    public Optional<QueryResult> query(PlainQuery query) {
        DomainAsserts.assertArgumentNotNull(query, "Query must be defined.");
        DomainAsserts.assertArgumentNotEmpty(query.getDatasetName(), "Data set must be defined.");
        final var datasetName = DatasetName.of(query.getDatasetName());
        return repository.getByName(datasetName).flatMap(dataset -> {

            final var requestedColumnsTmp = mapToDatasetColumnNameCollection(dataset, query.getRequestedColumns());
            final var requestedColumnsNames = requestedColumnsTmp.isEmpty() ? dataset.getColumnNames() : requestedColumnsTmp;

            final var criteria = query.hasAnyFilterSet() ?
                    new SimpleQueryCriteria(datasetName, requestedColumnsNames, domainFilterMapper.map(dataset, query.getFilter()))
                    : new SimpleQueryCriteria(datasetName, requestedColumnsNames);

            return repository.simpleQuery(criteria);
        });
    }

    public Optional<QueryResult> analyticQuery(AnalyticQuery query) {
        DomainAsserts.assertArgumentNotNull(query, "Query must be defined.");
        DomainAsserts.assertArgumentNotEmpty(query.getDatasetName(), "Data set must be defined.");
        DomainAsserts.assertArgumentNotNull(query.getAggregation(), "Aggregation object must be defined.");
        DomainAsserts.assertArgumentNotNull(query.getAggregation().getOperationType(), "Aggregation operation type must be defined.");
        DomainAsserts.assertArgumentNotNull(query.getAggregation().getTargetColumnName(), "Aggregation target column type must be defined.");
        final var datasetName = DatasetName.of(query.getDatasetName());
        return repository.getByName(datasetName).flatMap(dataset -> {

            final var groupByColumns = mapToDatasetColumnNameCollection(dataset, query.getAggregation().getGroupByColumns());

            final var criteria = new AnalyticQueryCriteria(
                    datasetName,
                    query.getAggregation().getOperationType(),
                    getTargetColumnName(dataset, query),
                    buildVirtualColumnDefinition(dataset, query),
                    domainFilterMapper.map(dataset, query.getFilter()),
                    groupByColumns);
            return repository.analyticQuery(criteria);
        });
    }

    /**
     * Either targetColumn is a virtual column or it has to be among regular columns.
     *
     * @param dataset
     * @param query
     * @return
     */
    private DatasetColumnName getTargetColumnName(Dataset dataset, AnalyticQuery query) {
        final var targetColumnNameStr = query.getAggregation().getTargetColumnName();
        return Objects.nonNull(query.getVirtualColumnDefinition()) &&
                query.getVirtualColumnDefinition().getName().equals(targetColumnNameStr) ?
                DatasetColumnName.of(targetColumnNameStr) :
                findColumnDefinitionByName(dataset.getColumnDefinitions(), targetColumnNameStr).getName();
    }

    private Set<DatasetColumnName> mapToDatasetColumnNameCollection(Dataset dataset, Set<String> columnNames) {
        if (Objects.isNull(columnNames) || columnNames.isEmpty()) {
            return Set.of();
        }
        return columnNames.stream()
                .map(requestedColumn -> findColumnDefinitionByName(dataset.getColumnDefinitions(), requestedColumn).getName())
                .collect(Collectors.toSet());
    }

    private Optional<VirtualColumnDefinition> buildVirtualColumnDefinition(Dataset dataset, AnalyticQuery query) {
        return Optional.ofNullable(query.getVirtualColumnDefinition())
                .map(virtualCol -> new VirtualColumnDefinition(
                        DatasetColumnName.of(virtualCol.getName()),
                        findColumnDefinitionByName(dataset.getColumnDefinitions(), virtualCol.getOperand1Column()),
                        findColumnDefinitionByName(dataset.getColumnDefinitions(), virtualCol.getOperand2Column()),
                        virtualCol.getOperationType()
                ));
    }

}


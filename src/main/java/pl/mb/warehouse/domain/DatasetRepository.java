package pl.mb.warehouse.domain;

import java.util.Optional;
import java.util.Set;

public interface DatasetRepository {

    Dataset create(DatasetName name, DataFileSpecification fileSpecification);

    Set<DatasetName> getAllDatasetNames();

    Optional<Dataset> getByName(DatasetName name);

    Optional<QueryResult> simpleQuery(SimpleQueryCriteria criteria);

    Optional<QueryResult> analyticQuery(AnalyticQueryCriteria criteria);
}

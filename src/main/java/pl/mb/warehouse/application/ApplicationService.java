package pl.mb.warehouse.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mb.warehouse.domain.DataFileSpecification;
import pl.mb.warehouse.domain.Dataset;
import pl.mb.warehouse.domain.DatasetDomainService;
import pl.mb.warehouse.domain.DatasetName;
import pl.mb.warehouse.domain.QueryResult;
import pl.mb.warehouse.domain.dto.PlainQuery;
import pl.mb.warehouse.domain.dto.AnalyticQuery;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final DatasetDomainService domainService;

    public Dataset createDataSet(CreateDatasetCommand command) {
        return domainService.createDataset(
                DatasetName.of(command.getDatasetName()),
                DataFileSpecification.builder()
                        .location(command.getFileLocation())
                        .localDateFormat("MM/dd/yy")
                        .build()
        );
    }

    public Set<DatasetName> getAvailableDatasetNames() {
        return domainService.getAllDatasetNames();
    }

    public Optional<QueryResult> plainQuery(PlainQuery query) {
        return domainService.query(query);
    }

    public Optional<QueryResult> analyticQuery(AnalyticQuery query) {
        return domainService.analyticQuery(query);
    }

}

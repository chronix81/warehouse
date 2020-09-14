package pl.mb.warehouse.adapters.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mb.warehouse.application.ApplicationService;
import pl.mb.warehouse.application.CreateDatasetCommand;
import pl.mb.warehouse.domain.Dataset;
import pl.mb.warehouse.domain.DatasetName;
import pl.mb.warehouse.domain.QueryResult;
import pl.mb.warehouse.domain.dto.PlainQuery;
import pl.mb.warehouse.domain.dto.AnalyticQuery;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/warehouse/datasets")
@AllArgsConstructor
public class DatasetController {

    private final ApplicationService applicationService;

    @PostMapping
    public Dataset createDataset(@RequestBody CreateDatasetCommand command) {
        return applicationService.createDataSet(command);
    }

    @GetMapping
    public Set<DatasetName> getAllDatasetNames(){
        return applicationService.getAvailableDatasetNames();
    }

    @PostMapping("/plainQuery")
    public Optional<QueryResult> plainQuery(@RequestBody PlainQuery query) {
        return applicationService.plainQuery(query);
    }

    @PostMapping("/analyticQuery")
    public Optional<QueryResult> analyticQuery(@RequestBody AnalyticQuery query) {
        return applicationService.analyticQuery(query);
    }

}

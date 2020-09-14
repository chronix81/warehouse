package pl.mb.warehouse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnalyticQuery implements Query {
    private String datasetName;
    private VirtualColumnDto virtualColumnDefinition;
    private AggregationData aggregation;
    private Filter filter;
}

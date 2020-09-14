package pl.mb.warehouse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.mb.warehouse.domain.DataAggregationType;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AggregationData {
    private DataAggregationType operationType;
    private String targetColumnName;
    private Set<String> groupByColumns;
}

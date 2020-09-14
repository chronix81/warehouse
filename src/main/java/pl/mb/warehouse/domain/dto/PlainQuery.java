package pl.mb.warehouse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlainQuery implements Query {
    private String datasetName;
    private Set<String> requestedColumns;
    private Filter filter;

}

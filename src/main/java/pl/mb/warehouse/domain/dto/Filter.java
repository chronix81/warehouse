package pl.mb.warehouse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Filter {
    private Map<String, Set<String>> termFilters;
    private Map<String, DateRange> dateRangeFilters;

    public boolean hasAnyFilterSet() {
        return (Objects.nonNull(termFilters) && !termFilters.isEmpty()) ||
                        (Objects.nonNull(dateRangeFilters) && !dateRangeFilters.isEmpty());
    }
}

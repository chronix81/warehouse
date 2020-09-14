package pl.mb.warehouse.domain;

import org.springframework.stereotype.Service;
import pl.mb.warehouse.domain.dto.Filter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.mb.warehouse.domain.DatasetDomainUtils.findColumnDefinitionByName;

@Service
public class DomainFilterMapper {

    public Set<DatasetColumnFilter> map(Dataset dataset, Filter dto) {
        final var filters = new HashSet<DatasetColumnFilter>();
        if (Objects.isNull(dto)) {
            return filters;
        }

        if (Objects.nonNull(dto.getTermFilters())) {
            final var termFilters = dto.getTermFilters().entrySet().stream()
                    .map(entry -> {
                        final var columnDefinition = findColumnDefinitionByName(dataset.getColumnDefinitions(), entry.getKey());
                        if (columnDefinition.getType() != DatasetColumnType.TEXT) {
                            throw new IllegalArgumentDomainException("Term filter can be defined on text columns only");
                        }
                        return new TermFilter(columnDefinition.getName(), entry.getValue());
                    })
                    .collect(Collectors.toSet());
            filters.addAll(termFilters);
        }
        if (Objects.nonNull(dto.getDateRangeFilters())) {
            final var dateRangeFilters = dto.getDateRangeFilters().entrySet().stream()
                    .map(entry -> {
                        final var columnDefinition = findColumnDefinitionByName(dataset.getColumnDefinitions(), entry.getKey());
                        if (columnDefinition.getType() != DatasetColumnType.DATE) {
                            throw new IllegalArgumentDomainException("Date range filter can be defined on date columns only");
                        }
                        return new DateRangeFilter(columnDefinition.getName(), entry.getValue().getFrom(), entry.getValue().getTo());
                    })
                    .collect(Collectors.toSet());
            filters.addAll(dateRangeFilters);
        }
        return filters;
    }

}

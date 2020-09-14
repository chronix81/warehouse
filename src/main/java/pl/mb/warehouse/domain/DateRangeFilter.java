package pl.mb.warehouse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@ToString
public class DateRangeFilter extends DatasetColumnFilter {
    private final LocalDate min;
    private final LocalDate max;

    DateRangeFilter(DatasetColumnName columnName, LocalDate min, LocalDate max) {
        super(columnName);
        DomainAsserts.assertRange(min, max, "Date range boundaries must be defined and min cannot be greater than max.");
        this.min = min;
        this.max = max;
    }

}

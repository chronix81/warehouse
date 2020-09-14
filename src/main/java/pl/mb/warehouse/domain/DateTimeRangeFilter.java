package pl.mb.warehouse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@ToString
public class DateTimeRangeFilter extends DatasetColumnFilter {
    private final LocalDateTime min;
    private final LocalDateTime max;

    DateTimeRangeFilter(DatasetColumnName columnName, LocalDateTime min, LocalDateTime max) {
        super(columnName);
        DomainAsserts.assertRange(min, max, "DateTime range boundaries must be defined and min cannot be greater than max.");
        this.min = min;
        this.max = max;
    }

}

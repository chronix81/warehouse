package pl.mb.warehouse.domain;

import lombok.Getter;

import java.util.Set;

import static pl.mb.warehouse.domain.DatasetColumnType.INTEGER_NUMBER;
import static pl.mb.warehouse.domain.DatasetColumnType.REAL_NUMBER;

@Getter
public enum DataAggregationType {

    SUM(Set.of(INTEGER_NUMBER, REAL_NUMBER)),
    AVG(Set.of(INTEGER_NUMBER, REAL_NUMBER)),
    MIN(Set.of(INTEGER_NUMBER, REAL_NUMBER)),
    MAX(Set.of(INTEGER_NUMBER, REAL_NUMBER));

    DataAggregationType(Set<DatasetColumnType> allowedTypesForOperands) {
        this.allowedTypesForOperands = allowedTypesForOperands;
    }

    private Set<DatasetColumnType> allowedTypesForOperands;
}

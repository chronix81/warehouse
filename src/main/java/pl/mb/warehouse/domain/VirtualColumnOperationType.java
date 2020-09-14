package pl.mb.warehouse.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum VirtualColumnOperationType {
    DIVISION(DatasetColumnType.REAL_NUMBER, Set.of(DatasetColumnType.INTEGER_NUMBER, DatasetColumnType.REAL_NUMBER));

    VirtualColumnOperationType(DatasetColumnType virtualColumnType, Set<DatasetColumnType> allowedTypesForOperands) {
        this.virtualColumnType = virtualColumnType;
        this.allowedTypesForOperands = allowedTypesForOperands;
    }

    private DatasetColumnType virtualColumnType;
    private Set<DatasetColumnType> allowedTypesForOperands;
}

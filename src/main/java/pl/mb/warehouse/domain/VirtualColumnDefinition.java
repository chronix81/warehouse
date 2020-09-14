package pl.mb.warehouse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.stream.Stream;

/**
 * Defines a virtual column calculated ad hoc.
 */
@Getter
@EqualsAndHashCode
@ToString
public class VirtualColumnDefinition {
    private DatasetColumnDefinition resultingColumn;
    private DatasetColumnDefinition operand1;
    private DatasetColumnDefinition operand2;
    private VirtualColumnOperationType virtualColumnOperationType;

    public VirtualColumnDefinition(DatasetColumnName resultingColumnName,
                                   DatasetColumnDefinition operand1, DatasetColumnDefinition operand2, VirtualColumnOperationType virtualColumnOperationType) {
        DomainAsserts.assertArgumentNotNull(resultingColumnName, "Column name must be specified when defining virtual column.");
        DomainAsserts.assertArgumentNotNull(operand1, "First operand must be specified when defining virtual column.");
        DomainAsserts.assertArgumentNotNull(operand2, "Second operand name must be specified when defining virtual column.");
        DomainAsserts.assertArgumentNotNull(virtualColumnOperationType, "Operation tye must be specified when defining virtual column.");
        if(!Stream.of(operand1.getType(), operand2.getType())
                .allMatch(type -> virtualColumnOperationType.getAllowedTypesForOperands().contains(type))) {
            throw new IllegalArgumentDomainException("Unsupported operands types for given operation.");
        }
        this.resultingColumn = new DatasetColumnDefinition(resultingColumnName, virtualColumnOperationType.getVirtualColumnType());
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.virtualColumnOperationType = virtualColumnOperationType;
    }

}

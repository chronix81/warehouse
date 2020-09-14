package pl.mb.warehouse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.mb.warehouse.domain.VirtualColumnOperationType;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VirtualColumnDto {
    private String name;
    private String operand1Column;
    private String operand2Column;
    private VirtualColumnOperationType operationType;
}

package pl.mb.warehouse.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateDatasetCommand {
    private String datasetName;
    private String fileLocation;
}

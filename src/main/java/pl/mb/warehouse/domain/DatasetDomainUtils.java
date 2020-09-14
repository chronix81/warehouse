package pl.mb.warehouse.domain;


import java.util.Collection;
import java.util.NoSuchElementException;

class DatasetDomainUtils {
    static DatasetColumnDefinition findColumnDefinitionByName(Collection<DatasetColumnDefinition> columnsInDataset, String requestedColumnName) {
        final var requestedName = DatasetColumnName.of(requestedColumnName);
        try {
            return columnsInDataset.stream()
                    .filter(def -> def.getName().equals(requestedName))
                    .reduce((a, b) -> {
                        throw new DomainException("Duplicated columns in dataset: " + a + ", " + b);
                    })
                    .get();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentDomainException("Requested column doesn't exist in a dataset. Column name: " + requestedColumnName);
        }
    }
}

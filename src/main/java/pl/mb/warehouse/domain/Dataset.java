package pl.mb.warehouse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(of = {"name"})
@ToString
public class Dataset {

    private final DatasetName name;
    private final Set<DatasetColumnDefinition> columnDefinitions;

    public Dataset(DatasetName name, Set<DatasetColumnDefinition> columnDefinitions) {
        DomainAsserts.assertCollectionNotEmpty(columnDefinitions, "Data set name and column definitions are required to create a data set.");
        this.name = name;
        this.columnDefinitions = Collections.unmodifiableSet(columnDefinitions);
    }

    @JsonIgnore
    public Set<DatasetColumnName> getColumnNames() {
        return columnDefinitions.stream().map(def -> def.getName()).collect(Collectors.toUnmodifiableSet());
    }

}

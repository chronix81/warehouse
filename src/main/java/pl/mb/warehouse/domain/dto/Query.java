package pl.mb.warehouse.domain.dto;

import java.util.Objects;

public interface Query {
    String getDatasetName();
    Filter getFilter();


    default boolean hasAnyFilterSet() {
        return Objects.nonNull(getFilter()) && getFilter().hasAnyFilterSet();
    }

}

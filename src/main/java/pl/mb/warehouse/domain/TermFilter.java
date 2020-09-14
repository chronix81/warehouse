package pl.mb.warehouse.domain;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;

@Getter

public class TermFilter extends DatasetColumnFilter {
    private final Set<String> terms;

    TermFilter(DatasetColumnName columnName, Set<String> terms) {
        super(columnName);
        DomainAsserts.assertCollectionNotEmpty(terms, "No terms passed.");
        this.terms = Collections.unmodifiableSet(terms);
    }


}

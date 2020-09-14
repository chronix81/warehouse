package pl.mb.warehouse.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.RequiredArgsConstructor;


//fixme it can be handled so much better, hopefully it's enough for the task
@RequiredArgsConstructor
@Data
public class QueryResult {
    @JsonValue
    private final String[][] result;
}

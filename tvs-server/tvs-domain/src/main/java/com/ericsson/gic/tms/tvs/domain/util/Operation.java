package com.ericsson.gic.tms.tvs.domain.util;

import java.util.function.BiFunction;

public enum Operation {
    EQUALS("=", EqualsFilter::new),
    NOT_EQUALS(null, NotEqualsFilter::new),
    GREATER_THAN(">", GreaterThanFilter::new),
    LESS_THAN("<", LessThanFilter::new),
    RANGE(null, RangeFilter::new),
    CONTAINS("~", ContainsFilter::new),
    IN(null, InFilter::new);

    private String operationSymbol;
    private BiFunction<String, Object, Filter> function;

    Operation(String operationSymbol, BiFunction<String, Object, Filter> function) {
        this.operationSymbol = operationSymbol;
        this.function = function;
    }

    public String getOperationSymbol() {
        return operationSymbol;
    }

    public Filter createFilter(String field, Object value) {
        return function.apply(field, value);
    }
}

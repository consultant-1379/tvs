package com.ericsson.gic.tms.tvs.domain.util;

public class EqualsFilter extends Filter {

    public EqualsFilter(String field, Object value) {
        super(field, value);
    }

    @Override
    public Operation getOperation() {
        return Operation.EQUALS;
    }

    @Override
    public String getExpression() {
        return getField() + ": #";
    }
}

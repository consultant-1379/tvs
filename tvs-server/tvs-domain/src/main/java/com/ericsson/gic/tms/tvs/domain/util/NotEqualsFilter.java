package com.ericsson.gic.tms.tvs.domain.util;

public class NotEqualsFilter extends EqualsFilter {

    public NotEqualsFilter(String field, Object value) {
        super(field, value);
    }

    @Override
    public Operation getOperation() {
        return Operation.NOT_EQUALS;
    }

    @Override
    public String getExpression() {
        return getField() + ": {$ne: #}";
    }
}

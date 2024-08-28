package com.ericsson.gic.tms.tvs.domain.util;

public class GreaterThanFilter extends Filter {

    public GreaterThanFilter(String field, Object value) {
        super(field, value);
    }

    @Override
    public Operation getOperation() {
        return Operation.GREATER_THAN;
    }

    @Override
    public String getExpression() {
        return getField() + ": {$gt: #}";
    }
}

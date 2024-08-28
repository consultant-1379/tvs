package com.ericsson.gic.tms.tvs.domain.util;

public class LessThanFilter extends Filter {

    public LessThanFilter(String field, Object value) {
        super(field, value);
    }

    @Override
    public Operation getOperation() {
        return Operation.LESS_THAN;
    }

    @Override
    public String getExpression() {
        return getField() + ": {$lt: #}";
    }
}

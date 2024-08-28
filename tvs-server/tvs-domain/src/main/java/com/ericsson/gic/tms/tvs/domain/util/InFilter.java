package com.ericsson.gic.tms.tvs.domain.util;

public class InFilter extends Filter {

    public InFilter(String field, Object value) {
        super(field, value);
    }

    @Override
    public Operation getOperation() {
        return Operation.IN;
    }

    @Override
    public String getExpression() {
        return getField() + ": {$in: #}";
    }
}

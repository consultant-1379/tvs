package com.ericsson.gic.tms.tvs.domain.util;

public class RangeFilter extends Filter {

    public RangeFilter(String field, Object value) {
        super(field, value);
    }

    public RangeFilter(String field, Object value, Object value2) {
        super(field, new Object[] {value, value2});
    }

    @Override
    public Operation getOperation() {
        return Operation.RANGE;
    }

    @Override
    public String getExpression() {
        return getField() + ": {$gt: #, $lt: #}";
    }
}

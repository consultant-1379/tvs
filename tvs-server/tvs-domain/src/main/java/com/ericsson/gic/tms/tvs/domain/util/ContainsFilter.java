package com.ericsson.gic.tms.tvs.domain.util;

import java.util.regex.Pattern;

public class ContainsFilter extends Filter {

    public ContainsFilter(String field, Object value) {
        super(field, value);
    }

    @Override
    public Operation getOperation() {
        return Operation.CONTAINS;
    }

    @Override
    public String getExpression() {
        return getField() + ": {$regex: #, $options: 'i'}";
    }

    @Override
    public Object getValues() {
        return Pattern.quote(super.getValues().toString());
    }
}

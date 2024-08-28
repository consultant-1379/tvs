package com.ericsson.gic.tms.tvs.domain.util;

public abstract class Filter implements MongoExpression {
    private String field;
    private Object value;

    public Filter(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public abstract Operation getOperation();

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValues() {
        return value;
    }

}

package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import javax.validation.constraints.NotNull;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         20/01/2017
 */
public class RuleBean implements Attributes<String> {

    private String id;

    @NotNull
    private String field;

    @NotNull
    private String fieldType;

    @NotNull
    private String operation;

    @NotNull
    private Object value;

    public RuleBean() {
        //needed for parsing
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}

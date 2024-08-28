package com.ericsson.gic.tms.tvs.domain.model.verdict.notification;

import java.util.Date;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         24/01/2017
 */
public class Criteria {

    private String id;

    private String field;

    private String fieldType;

    private String operation;

    private Object value;

    private Object lastTriggeredValue;

    private Date lastTriggeredDate;

    public Criteria() {
        // needed for parsing
    }

    public Criteria(String field, String fieldType, String operation, Object value) {
        this.field = field;
        this.fieldType = fieldType;
        this.operation = operation;
        this.value = value;
    }

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

    public Object getLastTriggeredValue() {
        return lastTriggeredValue;
    }

    public void setLastTriggeredValue(Object lastTriggeredValue) {
        this.lastTriggeredValue = lastTriggeredValue;
    }

    public Date getLastTriggeredDate() {
        return lastTriggeredDate;
    }

    public void setLastTriggeredDate(Date lastTriggeredDate) {
        this.lastTriggeredDate = lastTriggeredDate;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", field, operation, value);
    }
}

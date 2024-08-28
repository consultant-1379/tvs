package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FieldType {
    BOOLEAN("Boolean"),
    DATE_TIME("DateTime"),
    DURATION("Duration"),
    STATUS("Status"),
    STATUS_SUCCESS("StatusSuccess"),
    STATUS_CANCELLED("StatusCancelled"),
    STATUS_BROKEN("StatusBroken"),
    STATUS_PENDING("StatusPending"),
    LINK("Link"),
    RATE("Rate"),
    NUMBER_SHORT("NumberShort"),
    NUMBER_LONG("NumberLong"),
    STRING("String");

    private String name;

    FieldType(String name) {
        this.name = name;
    }

    @JsonValue
    public String toValue() {
        return name;
    }
}

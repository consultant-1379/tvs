package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectRequirementType {
    PROJECT,
    MR,
    EPIC,
    STORY;

    @JsonValue
    public String toValue() {
        return this.name();
    }
}

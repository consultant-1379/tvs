package com.ericsson.gic.tms.tvs.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public abstract class AdditionalFieldAware {

    @JsonIgnore
    private Map<String, Object> additionalFields = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    @JsonAnySetter
    public void addAdditionalFields(String key, Object value) {
        if (value != null) {
            this.additionalFields.put(key, value);
        }
    }

    public void addAdditionalFields(Map<String, Object> additionalFields) {
        if (additionalFields != null) {
            additionalFields.entrySet().forEach(entry ->
                this.addAdditionalFields(entry.getKey(), entry.getValue()));
        }
    }

    public void setAdditionalFields(Map<String, Object> additionalFields) {
        this.additionalFields = additionalFields;
    }
}

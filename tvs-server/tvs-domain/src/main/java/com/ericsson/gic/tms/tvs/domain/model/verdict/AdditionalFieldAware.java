package com.ericsson.gic.tms.tvs.domain.model.verdict;

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

    public Object getAdditionalField(String key) {
        return additionalFields.get(key);
    }

    @JsonAnySetter
    public void addAdditionalFields(String key, Object value) {
        this.additionalFields.put(key, value);
    }

    public void setAdditionalFields(Map<String, Object> additionalFields) {
        this.additionalFields = additionalFields;
    }
}

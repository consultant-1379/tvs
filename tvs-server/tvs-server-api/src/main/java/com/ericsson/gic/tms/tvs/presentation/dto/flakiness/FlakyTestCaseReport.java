package com.ericsson.gic.tms.tvs.presentation.dto.flakiness;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"testCaseId", "name", "flakiness", "longestDuration"})
public class FlakyTestCaseReport implements Attributes<String> {

    @JsonIgnore
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String testCaseId;

    /**
     * Ratio of test case status changes to amount of results, 0-100%
     */
    @JsonProperty
    private Integer flakiness;

    @JsonProperty
    private Long longestDuration;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public Integer getFlakiness() {
        return flakiness;
    }

    public void setFlakiness(Integer flakiness) {
        this.flakiness = flakiness;
    }

    public Long getLongestDuration() {
        return longestDuration;
    }

    public void setLongestDuration(Long longestDuration) {
        this.longestDuration = longestDuration;
    }

}

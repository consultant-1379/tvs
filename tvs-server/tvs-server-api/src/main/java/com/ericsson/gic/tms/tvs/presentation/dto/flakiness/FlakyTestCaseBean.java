package com.ericsson.gic.tms.tvs.presentation.dto.flakiness;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import java.util.List;
import java.util.Map;

public class FlakyTestCaseBean implements Attributes<String> {

    private String id;

    private String name;

    private String testCaseId;

    /**
     * Ratio of test case status changes to amount of results, 0-100%
     */
    private Integer flakiness;

    private Long longestDuration;

    private Map<String, FlakyTestCaseResultBean> latestResults;

    private List<String> suites;

    private List<String> components;

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

    public Map<String, FlakyTestCaseResultBean> getLatestResults() {
        return latestResults;
    }

    public void setLatestResults(Map<String, FlakyTestCaseResultBean> latestResults) {
        this.latestResults = latestResults;
    }

    public List<String> getSuites() {
        return suites;
    }

    public void setSuites(List<String> suites) {
        this.suites = suites;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }
}

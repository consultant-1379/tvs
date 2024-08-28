package com.ericsson.gic.tms.tvs.domain.model.verdict.flakiness;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FlakyTestCase {

    @MongoId
    private String id;

    private String name;

    private String testCaseId;

    private List<FlakyTestCaseResult> results;

    private List<String> suites;

    private List<List<String>> components;

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

    public List<FlakyTestCaseResult> getResults() {
        return results;
    }

    public void setResults(List<FlakyTestCaseResult> results) {
        this.results = results;
    }

    public List<String> getSuites() {
        return suites;
    }

    public void setSuites(List<String> suites) {
        this.suites = suites;
    }

    public List<List<String>> getComponents() {
        return components;
    }

    /**
     * Flattens and merges nested list of components.
     * Merging lists in MongoDB aggregation query (with double unwind and special handling of empty list) is too slow.
     */
    @JsonIgnore
    public List<String> getComponentsFlat() {
        Set<String> componentSet = this.components.stream()
            .flatMap(List::stream)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        return new ArrayList<>(componentSet);
    }

    public void setComponents(List<List<String>> components) {
        this.components = components;
    }
}

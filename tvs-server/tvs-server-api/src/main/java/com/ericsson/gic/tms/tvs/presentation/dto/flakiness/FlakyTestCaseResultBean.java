package com.ericsson.gic.tms.tvs.presentation.dto.flakiness;

import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;

import java.util.List;

public class FlakyTestCaseResultBean {

    private String testCaseId;

    private String name;

    private String resultCode;

    private ExecutionTimeBean time;

    private int executionCount;

    private List<String> suites;

    private List<String> components;

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public ExecutionTimeBean getTime() {
        return time;
    }

    public void setTime(ExecutionTimeBean time) {
        this.time = time;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(int executionCount) {
        this.executionCount = executionCount;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public List<String> getSuites() {
        return suites;
    }

    public void setSuites(List<String> suites) {
        this.suites = suites;
    }
}

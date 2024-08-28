package com.ericsson.gic.tms.tvs.domain.model.verdict.flakiness;

import com.ericsson.gic.tms.tvs.domain.model.verdict.ExecutionTime;

public class FlakyTestCaseResult {

    private String testCaseId;

    private String name;

    private String executionId;

    private String resultCode;

    private ExecutionTime time;

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

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public ExecutionTime getTime() {
        return time;
    }

    public void setTime(ExecutionTime time) {
        this.time = time;
    }
}

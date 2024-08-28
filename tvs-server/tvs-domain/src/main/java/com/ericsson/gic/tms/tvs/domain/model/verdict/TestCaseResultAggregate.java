package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.google.common.base.MoreObjects;

import java.util.Date;

public class TestCaseResultAggregate {

    private String testSuiteResultId;

    private String testCaseId;

    private long averageDuration;

    private int successCount;

    private int failureCount;

    private int errorCount;

    private int executionCount;

    private Date lastExecutionDate;

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public long getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(long averageDuration) {
        this.averageDuration = averageDuration;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(int executionCount) {
        this.executionCount = executionCount;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public String getTestSuiteResultId() {
        return testSuiteResultId;
    }

    public void setTestSuiteResultId(String testSuiteResultId) {
        this.testSuiteResultId = testSuiteResultId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("testSuiteResultId", testSuiteResultId)
            .add("testCaseId", testCaseId)
            .add("averageDuration", averageDuration)
            .add("successCount", successCount)
            .add("failureCount", failureCount)
            .add("errorCount", errorCount)
            .add("executionCount", executionCount)
            .add("lastExecutionDate", lastExecutionDate)
            .toString();
    }
}

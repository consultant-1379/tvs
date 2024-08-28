package com.ericsson.gic.tms.tvs.domain.model.verdict;

import java.util.Date;

public class JobAggregation {

    private int testSessionCount;

    private int lastTestSessionTestSuiteCount;

    private int lastTestSessionTestCaseCount;

    private Date lastExecutionDate;

    private long lastTestSessionDuration;

    private long avgTestSessionDuration;

    public int getTestSessionCount() {
        return testSessionCount;
    }

    public void setTestSessionCount(int testSessionCount) {
        this.testSessionCount = testSessionCount;
    }

    public int getLastTestSessionTestSuiteCount() {
        return lastTestSessionTestSuiteCount;
    }

    public void setLastTestSessionTestSuiteCount(int lastTestSessionTestSuiteCount) {
        this.lastTestSessionTestSuiteCount = lastTestSessionTestSuiteCount;
    }

    public int getLastTestSessionTestCaseCount() {
        return lastTestSessionTestCaseCount;
    }

    public void setLastTestSessionTestCaseCount(int lastTestSessionTestCaseCount) {
        this.lastTestSessionTestCaseCount = lastTestSessionTestCaseCount;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public long getLastTestSessionDuration() {
        return lastTestSessionDuration;
    }

    public void setLastTestSessionDuration(long lastTestSessionDuration) {
        this.lastTestSessionDuration = lastTestSessionDuration;
    }

    public long getAvgTestSessionDuration() {
        return avgTestSessionDuration;
    }

    public void setAvgTestSessionDuration(long avgTestSessionDuration) {
        this.avgTestSessionDuration = avgTestSessionDuration;
    }
}

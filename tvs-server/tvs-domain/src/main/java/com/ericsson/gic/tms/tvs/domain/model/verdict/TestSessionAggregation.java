package com.ericsson.gic.tms.tvs.domain.model.verdict;

import java.util.Date;

public class TestSessionAggregation {

    private int testCaseCount;

    private Date lastExecutionDate;

    private int testSuiteCount;

    private int passRate;

    public int getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public int getTestSuiteCount() {
        return testSuiteCount;
    }

    public void setTestSuiteCount(int testSuiteCount) {
        this.testSuiteCount = testSuiteCount;
    }

    public int getPassRate() {
        return passRate;
    }

    public void setPassRate(int passRate) {
        this.passRate = passRate;
    }
}

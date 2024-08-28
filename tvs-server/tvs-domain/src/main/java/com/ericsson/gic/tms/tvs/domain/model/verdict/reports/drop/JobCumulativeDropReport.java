package com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop;

import static com.google.common.base.MoreObjects.*;

public class JobCumulativeDropReport extends JobDropReport {
    private int failedTestCaseCount;
    private int passedTestCaseCount;

    public JobCumulativeDropReport() {
        // needed for parsing
    }

    public JobCumulativeDropReport(String dropName) {
        super(dropName);
    }

    public int getFailedTestCaseCount() {
        return failedTestCaseCount;
    }

    public void setFailedTestCaseCount(int failedTestCaseCount) {
        this.failedTestCaseCount = failedTestCaseCount;
    }

    public int getPassedTestCaseCount() {
        return passedTestCaseCount;
    }

    public void setPassedTestCaseCount(int passedTestCaseCount) {
        this.passedTestCaseCount = passedTestCaseCount;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
            .add("dropName", getDropName())
            .add("latestIsoVersion", getLatestIsoVersion())
            .add("passRate", getPassRate())
            .add("testCasesCount", getTestCasesCount())
            .add("failedTestCaseCount", failedTestCaseCount)
            .add("passedTestCaseCount", passedTestCaseCount)
            .add("testSessionsCount", getTestSessionsCount())
            .add("isoLastStartTime", getIsoLastStartTime())
            .toString();
    }
}

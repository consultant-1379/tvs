package com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop;

import static com.google.common.base.MoreObjects.*;

public class JobLastISODropReport extends JobDropReport {
    private int testSuitesCount;

    public JobLastISODropReport() {
        //needed for parsing
    }

    public JobLastISODropReport(String dropName) {
        super(dropName);
    }

    public int getTestSuitesCount() {
        return testSuitesCount;
    }

    public void setTestSuitesCount(int testSuitesCount) {
        this.testSuitesCount = testSuitesCount;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
            .add("dropName", getDropName())
            .add("latestIsoVersion", getLatestIsoVersion())
            .add("passRate", getPassRate())
            .add("testCasesCount", getTestCasesCount())
            .add("testSuitesCount", testSuitesCount)
            .add("testSessionsCount", getTestSessionsCount())
            .add("isoLastStartTime", getIsoLastStartTime())
            .toString();
    }
}

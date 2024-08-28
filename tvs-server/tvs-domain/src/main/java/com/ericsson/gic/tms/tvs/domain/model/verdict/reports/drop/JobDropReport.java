package com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop;

import java.util.Date;
import java.util.Objects;

public class JobDropReport {

    private String dropName;

    private String latestIsoVersion;

    private int passRate;

    private int testCasesCount;

    private int testSessionsCount;

    private Date isoLastStartTime;

    public JobDropReport() {
        // needed for parsing
    }

    public JobDropReport(String dropName) {
        this.dropName = dropName;
    }

    public Date getIsoLastStartTime() {
        return isoLastStartTime;
    }

    public void setIsoLastStartTime(Date isoLastStartTime) {
        this.isoLastStartTime = isoLastStartTime;
    }

    public String getDropName() {
        return dropName;
    }

    public void setDropName(String dropName) {
        this.dropName = dropName;
    }

    public String getLatestIsoVersion() {
        return latestIsoVersion;
    }

    public void setLatestIsoVersion(String latestIsoVersion) {
        this.latestIsoVersion = latestIsoVersion;
    }

    public int getPassRate() {
        return passRate;
    }

    public void setPassRate(int passRate) {
        this.passRate = passRate;
    }

    public int getTestCasesCount() {
        return testCasesCount;
    }

    public void setTestCasesCount(int testCasesCount) {
        this.testCasesCount = testCasesCount;
    }

    public int getTestSessionsCount() {
        return testSessionsCount;
    }

    public void setTestSessionsCount(int testSessionsCount) {
        this.testSessionsCount = testSessionsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDropReport that = (JobDropReport) o;

        return Objects.equals(dropName, that.dropName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dropName);
    }

}

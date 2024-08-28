package com.ericsson.gic.tms.tvs.presentation.dto.reports.drop;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import java.time.LocalDateTime;

public abstract class DropReportBean implements Attributes<String> {

    private String dropName;

    private String latestIsoVersion;

    private int passRate;

    private int testCasesCount;

    private int testSessionsCount;

    private LocalDateTime isoLastStartTime;

    @Override
    public String getId() {
        return dropName;
    }

    public void setDropName(String dropName) {
        this.dropName = dropName;
    }

    public String getDropName() {
        return dropName;
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

    public LocalDateTime getIsoLastStartTime() {
        return isoLastStartTime;
    }

    public void setIsoLastStartTime(LocalDateTime isoLastStartTime) {
        this.isoLastStartTime = isoLastStartTime;
    }
}

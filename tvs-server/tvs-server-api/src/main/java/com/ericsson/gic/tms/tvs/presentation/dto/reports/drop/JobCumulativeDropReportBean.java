package com.ericsson.gic.tms.tvs.presentation.dto.reports.drop;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JobCumulativeDropReportBean extends DropReportBean {
    private int failedTestCaseCount;
    private int passedTestCaseCount;

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

}

package com.ericsson.gic.tms.tvs.presentation.dto.reports.drop;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JobLastISODropReportBean extends DropReportBean {
    private int testSuitesCount;

    public int getTestSuitesCount() {
        return testSuitesCount;
    }

    public void setTestSuitesCount(int testSuitesCount) {
        this.testSuitesCount = testSuitesCount;
    }
}

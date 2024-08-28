package com.ericsson.gic.tms.tvs.presentation.dto.reports;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupedPassRateBean {

    private String groupBy;

    private int passRate;

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public int getPassRate() {
        return passRate;
    }

    public void setPassRate(int passRate) {
        this.passRate = passRate;
    }
}

package com.ericsson.gic.tms.tvs.presentation.dto.reports;

public class IsoGroupReportBean extends IsoReportBean<String> {

    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getId() {
        return group;
    }
}

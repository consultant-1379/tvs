package com.ericsson.gic.tms.tvs.presentation.dto.reports;

public class IsoComponentReportBean extends IsoReportBean<String> {

    private String component;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    @Override
    public String getId() {
        return component;
    }
}

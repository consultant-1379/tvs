package com.ericsson.gic.tms.tvs.presentation.dto.reports;

public class IsoPriorityReportBean extends IsoReportBean<String> {

    private String priority;

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String getId() {
        return priority;
    }
}

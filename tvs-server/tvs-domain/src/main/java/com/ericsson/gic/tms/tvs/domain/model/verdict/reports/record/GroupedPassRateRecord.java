package com.ericsson.gic.tms.tvs.domain.model.verdict.reports.record;

import static com.google.common.base.MoreObjects.toStringHelper;

public class GroupedPassRateRecord {

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

    @Override
    public String toString() {
        return toStringHelper(this)
            .add("groupBy", groupBy)
            .add("passRate", passRate)
            .toString();
    }
}

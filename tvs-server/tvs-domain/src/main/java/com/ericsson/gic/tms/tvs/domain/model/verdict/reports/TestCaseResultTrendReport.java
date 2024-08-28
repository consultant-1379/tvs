package com.ericsson.gic.tms.tvs.domain.model.verdict.reports;

import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.record.GroupedPassRateRecord;

import java.util.List;

import static com.google.common.base.MoreObjects.*;

public class TestCaseResultTrendReport {

    private String dropName;

    private String isoVersion;

    private List<GroupedPassRateRecord> data;

    public String getDropName() {
        return dropName;
    }

    public void setDropName(String dropName) {
        this.dropName = dropName;
    }

    public String getIsoVersion() {
        return isoVersion;
    }

    public void setIsoVersion(String isoVersion) {
        this.isoVersion = isoVersion;
    }

    public List<GroupedPassRateRecord> getData() {
        return data;
    }

    public void setData(List<GroupedPassRateRecord> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
            .add("dropName", dropName)
            .add("isoVersion", isoVersion)
            .add("data", data)
            .toString();
    }
}

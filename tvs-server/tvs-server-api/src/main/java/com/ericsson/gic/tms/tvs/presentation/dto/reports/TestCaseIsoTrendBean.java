package com.ericsson.gic.tms.tvs.presentation.dto.reports;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class TestCaseIsoTrendBean implements GenericReportBean<String> {

    private String dropName;

    private String isoVersion;

    private List<GroupedPassRateBean> data = new ArrayList<>();

    @Override
    public String getId() {
        return dropName;
    }

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

    public List<GroupedPassRateBean> getData() {
        return data;
    }

    public void setData(List<GroupedPassRateBean> data) {
        this.data = data;
    }
}

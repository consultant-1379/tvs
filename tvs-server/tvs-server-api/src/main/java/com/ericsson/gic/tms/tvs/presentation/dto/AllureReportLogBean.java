package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AllureReportLogBean implements Attributes<String> {

    @Null
    private String id;

    @NotNull
    private String jobExecutionId;

    public AllureReportLogBean() {
        // needed for parsing
    }

    public AllureReportLogBean(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(String jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }
}

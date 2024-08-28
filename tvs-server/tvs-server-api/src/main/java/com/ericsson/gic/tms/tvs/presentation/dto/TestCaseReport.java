package com.ericsson.gic.tms.tvs.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestCaseReport extends TestCaseResultReport {

    @JsonProperty("Test Activity")
    private String jobId;

    @JsonProperty("Session")
    private String executionId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
}

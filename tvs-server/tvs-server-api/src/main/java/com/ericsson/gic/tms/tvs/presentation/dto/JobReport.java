package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class JobReport implements Attributes<String> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @JsonProperty("Job ID")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Context")
    private String contextId;

    @JsonProperty("Test Session Count")
    private Integer testSessionCount;

    @JsonProperty("Last Session Test Suite Count")
    private Integer lastTestSessionTestSuiteCount;

    @JsonProperty("Last Session Test Case Count")
    private Integer lastTestSessionTestCaseCount;

    @JsonProperty("Last Execution Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date lastExecutionDate;

    @JsonProperty("Last Session Duration")
    private Long lastTestSessionDuration;

    @JsonProperty("Average Test Session Duration")
    private Long avgTestSessionDuration;

    @JsonProperty("Modified Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date modifiedDate;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Integer getTestSessionCount() {
        return testSessionCount;
    }

    public void setTestSessionCount(Integer testSessionCount) {
        this.testSessionCount = testSessionCount;
    }

    public Integer getLastTestSessionTestSuiteCount() {
        return lastTestSessionTestSuiteCount;
    }

    public void setLastTestSessionTestSuiteCount(Integer lastTestSessionTestSuiteCount) {
        this.lastTestSessionTestSuiteCount = lastTestSessionTestSuiteCount;
    }

    public Integer getLastTestSessionTestCaseCount() {
        return lastTestSessionTestCaseCount;
    }

    public void setLastTestSessionTestCaseCount(Integer lastTestSessionTestCaseCount) {
        this.lastTestSessionTestCaseCount = lastTestSessionTestCaseCount;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public Long getLastTestSessionDuration() {
        return lastTestSessionDuration;
    }

    public void setLastTestSessionDuration(Long lastTestSessionDuration) {
        this.lastTestSessionDuration = lastTestSessionDuration;
    }

    public Long getAvgTestSessionDuration() {
        return avgTestSessionDuration;
    }

    public void setAvgTestSessionDuration(Long avgTestSessionDuration) {
        this.avgTestSessionDuration = avgTestSessionDuration;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobReport jobReport = (JobReport) o;
        return id.equals(jobReport.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + contextId.hashCode();
        return result;
    }
}

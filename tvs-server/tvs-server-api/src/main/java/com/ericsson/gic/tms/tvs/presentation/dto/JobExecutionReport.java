package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class JobExecutionReport implements Attributes<String> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @JsonProperty("Job Execution ID")
    private String id;

    @JsonProperty("Status")
    private String resultCode;

    @JsonProperty("URI")
    private String uri;

    @JsonProperty("Start Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date startDate;

    @JsonProperty("Stop Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date stopDate;

    @JsonProperty("Duration")
    private Long duration;

    @JsonProperty("Pass Rate")
    private int passRate;

    @JsonProperty("Test Suite Count")
    private Integer testSuiteCount;

    @JsonProperty("Test Case Count")
    private Integer testCaseCount;

    @JsonProperty("Modified Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date modifiedDate;

    @JsonProperty("Created Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date createdDate;

    @JsonProperty("Last Execution Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date lastExecutionDate;

    @JsonProperty("ISO Version")
    private String isoVersion;

    @JsonProperty("ISO Artifact ID")
    private String isoArtifact;

    @JsonProperty("Drop Name")
    private String dropName;

    @JsonProperty("Jenkins Job Name")
    private String jobName;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public int getPassRate() {
        return passRate;
    }

    public void setPassRate(int passRate) {
        this.passRate = passRate;
    }

    public Integer getTestSuiteCount() {
        return testSuiteCount;
    }

    public void setTestSuiteCount(Integer testSuiteCount) {
        this.testSuiteCount = testSuiteCount;
    }

    public Integer getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(Integer testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public String getIsoVersion() {
        return isoVersion;
    }

    public void setIsoVersion(String isoVersion) {
        this.isoVersion = isoVersion;
    }

    public String getIsoArtifact() {
        return isoArtifact;
    }

    public void setIsoArtifact(String isoArtifact) {
        this.isoArtifact = isoArtifact;
    }

    public String getDropName() {
        return dropName;
    }

    public void setDropName(String dropName) {
        this.dropName = dropName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}

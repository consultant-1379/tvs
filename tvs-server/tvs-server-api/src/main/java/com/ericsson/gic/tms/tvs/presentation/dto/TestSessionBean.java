package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;
import java.util.Map;

@XmlRootElement
public class TestSessionBean extends AdditionalFieldAware implements Attributes<String>, HasTime, ResetFields {

    private String executionId;

    @Valid
    private ExecutionTimeBean time;

    private String uri;

    @Valid
    private List<TestSuiteResultBean> testSuites;

    @Null
    private Date modifiedDate;

    @Null
    private Date createdDate;

    private Map<String, String> logReferences;

    private String resultCode;

    private Integer testCaseCount;

    private Date lastExecutionDate;

    private Integer testSuiteCount;

    private Integer passRate;

    private boolean ignored;

    @Override
    public String getId() {
        return executionId;
    }

    public void setId(String id) {
        this.executionId = id;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    @Override
    public ExecutionTimeBean getTime() {
        return time;
    }

    public void setTime(ExecutionTimeBean time) {
        this.time = time;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<TestSuiteResultBean> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestSuiteResultBean> testSuites) {
        this.testSuites = testSuites;
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

    public Map<String, String> getLogReferences() {
        return logReferences;
    }

    public void setLogReferences(Map<String, String> logReferences) {
        this.logReferences = logReferences;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public Integer getPassRate() {
        return passRate;
    }

    public void setPassRate(Integer passRate) {
        this.passRate = passRate;
    }

    public Integer getTestSuiteCount() {
        return testSuiteCount;
    }

    public void setTestSuiteCount(Integer testSuiteCount) {
        this.testSuiteCount = testSuiteCount;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public Integer getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(Integer testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    @Override
    public void resetImmutableFields() {
        this.modifiedDate = null;
        this.createdDate = null;
        this.time.resetImmutableFields();
        this.testSuites.forEach(TestSuiteResultBean::resetImmutableFields);
    }
}

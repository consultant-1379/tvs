package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Date;
import java.util.Map;

public class TestSession extends AdditionalFieldAware implements AuditAware, HasExecutionTime, MongoEntity<String> {

    @MongoId
    @MongoObjectId
    private String id;

    private String jobId;

    private String executionId;

    private Map<String, String> logReferences;

    private String resultCode;

    private String uri;

    private Date modifiedDate;

    private Date createdDate;

    private ExecutionTime time;

    private Integer testCaseCount;

    private Date lastExecutionDate;

    private Integer testSuiteCount;

    private Integer passRate;

    private boolean ignored;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public ExecutionTime getTime() {
        return time;
    }

    public void setTime(ExecutionTime time) {
        this.time = time;
    }

    public Integer getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(Integer testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public Integer getTestSuiteCount() {
        return testSuiteCount;
    }

    public void setTestSuiteCount(Integer testSuiteCount) {
        this.testSuiteCount = testSuiteCount;
    }

    public Integer getPassRate() {
        return passRate;
    }

    public void setPassRate(Integer passRate) {
        this.passRate = passRate;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}

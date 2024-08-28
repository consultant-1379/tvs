package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Date;

public class TestCaseResult extends AdditionalFieldAware implements MongoEntity<String>, AuditAware, HasExecutionTime {

    @MongoId
    @MongoObjectId
    private String id;

    private String contextId;

    private String jobId;

    private String executionId;

    private String testSuiteName;

    private String testSuiteResultId;

    private String testCaseId;

    private String name;

    private String resultCode;

    private String externalResultCode;

    private String source;

    private ImportStatus importStatus;

    private Date modifiedDate;

    private Date createdDate;

    private ExecutionTime time;

    private boolean ignored;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
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

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public String getTestSuiteResultId() {
        return testSuiteResultId;
    }

    public void setTestSuiteResultId(String testSuiteResultId) {
        this.testSuiteResultId = testSuiteResultId;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getExternalResultCode() {
        return externalResultCode;
    }

    public void setExternalResultCode(String externalResultCode) {
        this.externalResultCode = externalResultCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ImportStatus getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
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
}

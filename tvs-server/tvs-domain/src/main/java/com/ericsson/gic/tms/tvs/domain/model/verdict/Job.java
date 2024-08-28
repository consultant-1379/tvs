package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Date;

public class Job extends AdditionalFieldAware implements MongoEntity<String>, AuditAware {

    @MongoId
    @MongoObjectId
    private String id;

    private String uid;

    private String name;

    private String contextId;

    private Date modifiedDate;

    private int testSessionCount;

    private int lastTestSessionTestSuiteCount;

    private int lastTestSessionTestCaseCount;

    private Date lastExecutionDate;

    private long lastTestSessionDuration;

    private long avgTestSessionDuration;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    @Override
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public Date getCreatedDate() {
        return null;
    }

    @Override
    public void setCreatedDate(Date time) {
        // not used
    }

    @Override
    public void setModifiedDate(Date time) {
        this.modifiedDate = time;
    }

    public int getTestSessionCount() {
        return testSessionCount;
    }

    public void setTestSessionCount(int testSessionCount) {
        this.testSessionCount = testSessionCount;
    }

    public int getLastTestSessionTestSuiteCount() {
        return lastTestSessionTestSuiteCount;
    }

    public void setLastTestSessionTestSuiteCount(int lastTestSessionTestSuiteCount) {
        this.lastTestSessionTestSuiteCount = lastTestSessionTestSuiteCount;
    }

    public int getLastTestSessionTestCaseCount() {
        return lastTestSessionTestCaseCount;
    }

    public void setLastTestSessionTestCaseCount(int lastTestSessionTestCaseCount) {
        this.lastTestSessionTestCaseCount = lastTestSessionTestCaseCount;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public long getLastTestSessionDuration() {
        return lastTestSessionDuration;
    }

    public void setLastTestSessionDuration(long lastTestSessionDuration) {
        this.lastTestSessionDuration = lastTestSessionDuration;
    }

    public long getAvgTestSessionDuration() {
        return avgTestSessionDuration;
    }

    public void setAvgTestSessionDuration(long avgTestSessionDuration) {
        this.avgTestSessionDuration = avgTestSessionDuration;
    }

}

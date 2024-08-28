package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class JobBean extends AdditionalFieldAware implements Attributes<String>, ResetFields {

    @Null
    private String id;

    @Null
    private String name;

    private String context;

    @Valid
    @NotNull
    private List<TestSessionBean> testSessions;

    @Null
    private Date modifiedDate;

    @Null
    private Integer testSessionCount;

    @Null
    private Integer lastTestSessionTestSuiteCount;

    @Null
    private Integer lastTestSessionTestCaseCount;

    @Null
    private Date lastExecutionDate;

    @Null
    private Long lastTestSessionDuration;

    @Null
    private Long avgTestSessionDuration;

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

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<TestSessionBean> getTestSessions() {
        return testSessions;
    }

    public void setTestSessions(List<TestSessionBean> testSessions) {
        this.testSessions = testSessions;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
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

    @Override
    public void resetImmutableFields() {
        this.id = null;
        this.name = null;
        this.context = null;
        this.modifiedDate = null;
        this.testSessionCount = null;
        this.lastTestSessionTestSuiteCount = null;
        this.lastTestSessionTestCaseCount = null;
        this.lastExecutionDate = null;
        this.lastTestSessionDuration = null;
        this.avgTestSessionDuration = null;

        testSessions.forEach(TestSessionBean::resetImmutableFields);
    }
}

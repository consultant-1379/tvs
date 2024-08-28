package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class TestSuiteResultBean extends AdditionalFieldAware implements Attributes<String>, HasTime, ResetFields {

    private String name;

    @Valid
    @NotNull
    private ExecutionTimeBean time;

    @Valid
    private StatisticsBean statistics;

    @Valid
    @NotNull
    private List<TestCaseResultBean> testCaseResults;

    @Null
    private Date modifiedDate;

    @Null
    private Date createdDate;

    @Null
    private Integer passRate;

    @Override
    public String getId() {
        return name;
    }

    public void setId(String id) {
        this.name = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ExecutionTimeBean getTime() {
        return time;
    }

    public void setTime(ExecutionTimeBean time) {
        this.time = time;
    }

    public StatisticsBean getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsBean statistics) {
        this.statistics = statistics;
    }

    public List<TestCaseResultBean> getTestCaseResults() {
        return testCaseResults;
    }

    public void setTestCaseResults(List<TestCaseResultBean> testCaseResults) {
        this.testCaseResults = testCaseResults;
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

    public Integer getPassRate() {
        return passRate;
    }

    public void setPassRate(Integer passRate) {
        this.passRate = passRate;
    }

    @Override
    public void resetImmutableFields() {
        this.modifiedDate = null;
        this.createdDate = null;
        this.passRate = null;
        this.time.resetImmutableFields();
        this.testCaseResults.forEach(TestCaseResultBean::resetImmutableFields);
    }
}

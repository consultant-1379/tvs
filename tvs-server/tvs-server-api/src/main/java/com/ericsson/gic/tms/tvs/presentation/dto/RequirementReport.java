package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class RequirementReport implements Attributes<String> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @JsonProperty("Requirement ID")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("EPIC count")
    private int epicCount;

    @JsonProperty("Test Case Count")
    private Integer testCaseCount;

    @JsonProperty("Test Story Count")
    private Integer userStoryCount;

    @JsonProperty("User Story With Results")
    private Integer userStoryWithResults;

    @JsonProperty("SOC")
    private Double soc;

    @JsonProperty("SOV")
    private Double sov;

    @JsonProperty("Modified Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date modifiedDate;

    @JsonProperty("Created Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date createdDate;

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

    public int getEpicCount() {
        return epicCount;
    }

    public void setEpicCount(int epicCount) {
        this.epicCount = epicCount;
    }

    public Integer getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(Integer testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public Integer getUserStoryCount() {
        return userStoryCount;
    }

    public void setUserStoryCount(Integer userStoryCount) {
        this.userStoryCount = userStoryCount;
    }

    public Integer getUserStoryWithResults() {
        return userStoryWithResults;
    }

    public void setUserStoryWithResults(Integer userStoryWithResults) {
        this.userStoryWithResults = userStoryWithResults;
    }

    public Double getSoc() {
        return soc;
    }

    public void setSoc(Double soc) {
        this.soc = soc;
    }

    public Double getSov() {
        return sov;
    }

    public void setSov(Double sov) {
        this.sov = sov;
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
}

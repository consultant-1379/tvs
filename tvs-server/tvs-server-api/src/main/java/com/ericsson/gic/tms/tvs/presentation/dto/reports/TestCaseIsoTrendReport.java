package com.ericsson.gic.tms.tvs.presentation.dto.reports;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "dropName", "isoVersion", "passRate"})
public class TestCaseIsoTrendReport implements Attributes<String> {

    @JsonProperty("Title")
    private String id;

    @JsonProperty("DropName")
    private String dropName;

    @JsonProperty("IsoVersion")
    private String isoVersion;

    @JsonProperty("PassRate")
    private Integer passRate;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsoVersion() {
        return isoVersion;
    }

    public void setIsoVersion(String isoVersion) {
        this.isoVersion = isoVersion;
    }

    public String getDropName() {
        return dropName;
    }

    public void setDropName(String dropName) {
        this.dropName = dropName;
    }

    public Integer getPassRate() {
        return passRate;
    }

    public void setPassRate(Integer passRate) {
        this.passRate = passRate;
    }
}

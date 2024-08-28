package com.ericsson.gic.tms.tvs.presentation.dto.reports;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "total", "passed", "failed", "broken", "cancelled", "passRate"})
public class IsoCsvReport implements Attributes<String> {

    @JsonProperty("Title")
    private String id;

    @JsonProperty("Total")
    private int total;

    @JsonProperty("Passed")
    private int passed;

    @JsonProperty("Cancelled")
    private int cancelled;

    @JsonProperty("Failed")
    private int failed;

    @JsonProperty("Broken")
    private int broken;

    @JsonProperty("passRate")
    private int passRate;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getCancelled() {
        return cancelled;
    }

    public void setCancelled(int cancelled) {
        this.cancelled = cancelled;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getBroken() {
        return broken;
    }

    public void setBroken(int broken) {
        this.broken = broken;
    }

    public int getPassRate() {
        return passRate;
    }

    public void setPassRate(int passRate) {
        this.passRate = passRate;
    }
}

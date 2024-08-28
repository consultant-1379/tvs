package com.ericsson.gic.tms.tvs.presentation.dto.reports;

import javax.ws.rs.NotFoundException;
import java.util.Objects;

public enum ReportType {

    DROP_TABLE("drop-report-table"),
    DROP_CHART("drop-report-chart"),
    ISO_PRIORITY("iso-priority"),
    ISO_GROUP("iso-group"),
    ISO_COMPONENT("iso-component"),
    TREND_PRIORITY("trend-priority"),
    TREND_GROUP("trend-group"),
    TREND_COMPONENT("trend-component"),
    TREND_REQUIREMENT("trend-requirement"),
    TREND_SUITE("trend-suite");

    private String name;

    ReportType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ReportType fromString(String reportId) {
        for (ReportType type : ReportType.values()) {
            if (Objects.equals(reportId, type.getName())) {
                return type;
            }
        }
        throw new NotFoundException("Unable to find report with ID: " + reportId);
    }
}

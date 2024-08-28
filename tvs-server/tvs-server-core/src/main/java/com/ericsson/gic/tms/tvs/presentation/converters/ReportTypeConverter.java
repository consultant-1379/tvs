package com.ericsson.gic.tms.tvs.presentation.converters;

import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.ReportType;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.ParamConverter;

@Component
public class ReportTypeConverter implements ParamConverter<ReportType> {

    @Override
    public ReportType fromString(String reportType) {
        ReportType testCaseReportType;
        try {
            testCaseReportType = ReportType.fromString(reportType);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new NotFoundException();
        }
        return testCaseReportType;
    }

    @Override
    public String toString(ReportType reportType) {
        return reportType.getName();
    }
}

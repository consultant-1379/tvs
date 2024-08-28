package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.DropReportService;
import com.ericsson.gic.tms.tvs.application.services.QueryService;
import com.ericsson.gic.tms.tvs.application.services.SortService;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.ReportType;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportType;
import com.ericsson.gic.tms.tvs.presentation.resources.JobReportResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class JobReportController extends AbstractJsonApiCapableController implements JobReportResource {

    @Autowired
    private DropReportService dropReportService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private SortService sortService;

    @Override
    public DocumentList<? extends DropReportBean> getData(String jobId, String reportId, LocalDateTime since,
                                                          LocalDateTime until, int page, int size, String orderBy,
                                                          SortingMode orderMode, String query,
                                                          DropReportType reportType) {
        ReportType reportPresentationType = ReportType.fromString(reportId);
        Query parsedQuery = queryService.createQuery(query);

        if (isPaginatedRequest(reportPresentationType)) {
            PageRequest pageRequest = new PageRequest(page - 1, size,
                sortService.getSort(orderMode.toString(), orderBy));

            Page<? extends DropReportBean> dropData =
                dropReportService.getPaginatedDropReportData(jobId, since, until, pageRequest, parsedQuery, reportType);

            return responseFor(dropData.getContent())
                .withSelfRel(JobReportResource.class)
                .withPagination(JobReportResource.class, dropData, pageRequest)
                .build();
        } else {
            return responseFor(dropReportService.getAllDropReportData(jobId, since, until, parsedQuery, reportType))
                .withSelfRel(JobReportResource.class)
                .build();
        }
    }

    private static boolean isPaginatedRequest(ReportType reportType) {
        switch (reportType) {
            case DROP_TABLE:
                return true;
            default:
                return false;
        }
    }
}

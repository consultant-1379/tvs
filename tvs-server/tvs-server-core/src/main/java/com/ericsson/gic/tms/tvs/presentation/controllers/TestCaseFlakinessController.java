package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.TestCaseFlakinessService;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseBean;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseReport;
import com.ericsson.gic.tms.tvs.presentation.resources.TestCaseFlakinessResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;

@Controller
public class TestCaseFlakinessController
    extends AbstractJsonApiCapableController
    implements TestCaseFlakinessResource {

    private static final String CSV_FILE_NAME = "inline; filename=\"flakiness-report-%s.csv\"";

    @Autowired
    private TestCaseFlakinessService testCaseFlakinessService;

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public DocumentList<FlakyTestCaseBean> getAllTestCaseFlakinessByJob(String jobId,
                                                                        LocalDateTime startTime,
                                                                        LocalDateTime endTime,
                                                                        int size) {
        List<FlakyTestCaseBean> flakyTestCases =
                testCaseFlakinessService.getTestCaseFlakinessByJob(jobId, startTime, endTime, size);
        return responseFor(flakyTestCases)
            .withSelfRel(TestCaseFlakinessResource.class)
            .build();
    }

    @Override
    public Document<FlakyTestCaseBean> getSingleTestCaseFlakiness(String jobId,
                                                                  String testCaseName,
                                                                  LocalDateTime startTime,
                                                                  LocalDateTime endTime) {
        return responseFor(testCaseFlakinessService.getTestCaseFlakiness(jobId, testCaseName, startTime, endTime))
                .withSelfRel(TestCaseFlakinessResource.class)
                .build();
    }

    @Override
    public DocumentList<FlakyTestCaseReport> exportTestCaseFlakinessReportByJob(String jobId,
                                                                                LocalDateTime startTime,
                                                                                LocalDateTime endTime,
                                                                                int size) {
        List<FlakyTestCaseBean> flakyTestCases =
            testCaseFlakinessService.getTestCaseFlakinessByJob(jobId, startTime, endTime, size);

        List<FlakyTestCaseReport> report = mapperFacade.mapAsList(flakyTestCases, FlakyTestCaseReport.class);

        return responseFor(report)
            .withSelfRel(TestCaseFlakinessResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(CSV_FILE_NAME, jobId))
            .build();
    }
}

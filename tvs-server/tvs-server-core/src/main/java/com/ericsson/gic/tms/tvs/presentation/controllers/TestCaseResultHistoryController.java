package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.QueryService;
import com.ericsson.gic.tms.tvs.application.services.SortService;
import com.ericsson.gic.tms.tvs.application.services.TestCaseResultHistoryService;
import com.ericsson.gic.tms.tvs.application.services.TestCaseResultService;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseImportStatus;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultHistoryBean;
import com.ericsson.gic.tms.tvs.presentation.resources.TestCaseResultHistoryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

import static javax.ws.rs.core.HttpHeaders.*;

@Controller
public class TestCaseResultHistoryController extends AbstractJsonApiCapableController
    implements TestCaseResultHistoryResource {

    private static final String CSV_FILE_NAME_VALUE = "inline; filename=\"test-case-%s-results.csv\"";

    @Autowired
    private TestCaseResultHistoryService service;

    @Autowired
    private TestCaseResultService testCaseResultService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private SortService sortService;

    @Override
    public DocumentList<TestCaseResultBean> getList(TestCaseImportStatus importStatus, int page, int size) {
        PageRequest pageRequest = new PageRequest(page - 1, size);

        Page<TestCaseResultBean> testCaseResultHistory = service.getTestCaseResults(importStatus, pageRequest);

        return responseFor(testCaseResultHistory.getContent())
            .withSelfRel(TestCaseResultHistoryResource.class)
            .withPagination(TestCaseResultHistoryResource.class, testCaseResultHistory, pageRequest)
            .build();
    }

    @Override
    public DocumentList<TestCaseResultHistoryBean> getList(String testCaseId, LocalDateTime startTime,
                                                           LocalDateTime stopTime, int page, int size,
                                                           String orderBy, SortingMode orderMode, String query) {
        PageRequest pageRequest = new PageRequest(page - 1, size, sortService.getSort(orderMode.toString(), orderBy));

        Query parsedQuery = queryService.createQuery(query);

        Page<TestCaseResultHistoryBean> testCaseResultHistory =
            service.getTestCaseResultHistory(testCaseId, startTime, stopTime, pageRequest, parsedQuery);

        return responseFor(testCaseResultHistory.getContent())
            .withSelfRel(TestCaseResultHistoryResource.class)
            .withPagination(TestCaseResultHistoryResource.class, testCaseResultHistory, pageRequest)
            .build();
    }

    @Override
    public DocumentList<TestCaseReport> getList(String testCaseId) {
        return responseFor(service.getTestCase(testCaseId))
            .withSelfRel(TestCaseResultHistoryResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(CSV_FILE_NAME_VALUE, testCaseId))
            .build();
    }

    @Override
    public Document<TestCaseResultBean> update(String executionId, TestCaseResultBean testCaseResultBean) {
        return responseFor(testCaseResultService.updateTestCaseResult(executionId, testCaseResultBean))
            .withSelfRel(TestCaseResultHistoryResource.class)
            .build();
    }
}

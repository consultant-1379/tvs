package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.CollectionMetadataService;
import com.ericsson.gic.tms.tvs.application.services.QueryService;
import com.ericsson.gic.tms.tvs.application.services.SortService;
import com.ericsson.gic.tms.tvs.application.services.TestCaseResultService;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultReport;
import com.ericsson.gic.tms.tvs.presentation.resources.TestCaseResultResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;

@Controller
public class TestCaseResultController extends AbstractJsonApiCapableController implements TestCaseResultResource {

    private static final String CSV_FILE_NAME_VALUE = "inline; filename=\"activity-session-%s-test-case-results.csv\"";

    @Autowired
    private TestCaseResultService testCaseResultService;

    @Autowired
    private CollectionMetadataService collectionMetadataService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private SortService sortService;

    @Override
    public DocumentList<TestCaseResultBean> getList(String jobId, String executionId, String testSuiteName,
                                                    int page, int size, String orderBy, SortingMode orderMode,
                                                    String query) {
        //TODO: MongoDb does not have ignoreCase sorting :(
        PageRequest pageRequest = new PageRequest(page - 1, size, sortService.getSort(orderMode.toString(), orderBy));

        Query parsedQuery = queryService.createQuery(query);
        Page<TestCaseResultBean> testSessions = testCaseResultService
            .getPaginatedTestCases(jobId, executionId, testSuiteName, pageRequest, parsedQuery);

        return responseFor(testSessions.getContent())
            .withSelfRel(TestCaseResultResource.class)
            .withPagination(TestCaseResultResource.class, testSessions, pageRequest)
            .withMeta(COLUMNS, collectionMetadataService.getColumns(TEST_CASE_RESULT.getName()))
            .build();
    }

    @Override
    public DocumentList<TestCaseResultReport> getList(String jobId, String jobExecutionId, String testSuiteName) {
        return responseFor(testCaseResultService.findTestCaseResults(jobId, jobExecutionId, testSuiteName))
            .withSelfRel(TestCaseResultResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(CSV_FILE_NAME_VALUE, jobId))
            .build();
    }

    @Override
    public Document<TestCaseResultBean> getTestCaseResult(String jobId,
                                                          String testSessionExecutionId,
                                                          String testSuiteName,
                                                          String testCaseResultId) {
        TestCaseResultBean testCaseResult = testCaseResultService.getTestCaseResult(
            jobId,
            testSessionExecutionId,
            testSuiteName,
            testCaseResultId
        );
        return responseFor(testCaseResult)
            .withSelfRel(TestCaseResultResource.class)
            .build();
    }

}

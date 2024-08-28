package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.CollectionMetadataService;
import com.ericsson.gic.tms.tvs.application.services.QueryService;
import com.ericsson.gic.tms.tvs.application.services.SortService;
import com.ericsson.gic.tms.tvs.application.services.TestCaseResultService;
import com.ericsson.gic.tms.tvs.application.services.TestSessionService;
import com.ericsson.gic.tms.tvs.domain.model.TvsCollections;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.presentation.dto.JobExecutionReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode;
import com.ericsson.gic.tms.tvs.presentation.resources.JobResource;
import com.ericsson.gic.tms.tvs.presentation.resources.TestSessionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.COLUMNS;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;

@Controller
public class TestSessionController extends AbstractJsonApiCapableController implements TestSessionResource {

    private static final String CONTENT_DISPOSITION_VALUE = "inline; filename=\"activity-session-%s.csv\"";

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private TestCaseResultService testCaseResultService;

    @Autowired
    private CollectionMetadataService collectionMetadataService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private SortService sortService;

    @Override
    public DocumentList<TestSessionBean> getList(String jobId, LocalDateTime startTime,
                                                 LocalDateTime endTime, int page, int size, String orderBy,
                                                 SortingMode orderMode, String query) {
        //TODO: MongoDb does not have ignoreCase sorting :(
        PageRequest pageRequest = new PageRequest(page - 1, size, sortService.getSort(orderMode.toString(), orderBy));

        Query parsedQuery = queryService.createQuery(query);

        Page<TestSessionBean> testSessions = testSessionService
            .getPaginatedTestSessions(jobId, startTime, endTime, pageRequest, parsedQuery);

        return responseFor(testSessions.getContent())
            .withSelfRel(TestSessionResource.class)
            .withPagination(TestSessionResource.class, testSessions, pageRequest)
            .withMeta(COLUMNS, collectionMetadataService.getColumns(TvsCollections.TEST_SESSION.getName()))
            .build();
    }

    @Override
    public DocumentList<JobExecutionReport> getList(String jobId) {
        return responseFor(testSessionService.findJobExecutions(jobId))
            .withSelfRel(JobResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_VALUE, jobId))
            .build();
    }

    @Override
    public Document<TestSessionBean> getTestSession(String jobId, String executionId) {
        return responseFor(testSessionService.getTestSession(jobId, executionId))
            .withSelfRel(TestSessionResource.class, TEST_SESSION)
            .withMeta("children", testSessionService.getTestSessionChildren(jobId, executionId))
            .build();
    }

    @Override
    public Document<TestSessionBean> aggregateTestSession(String jobId, String executionId, TraversalMode mode) {
        return responseFor(testSessionService.aggregateTestSession(jobId, executionId, mode))
            .withSelfRel(TestSessionResource.class, TEST_SESSION_AGGREGATION)
            .build();
    }

    @Override
    public DocumentList<TestCaseResultReport> exportTestCaseResults(String jobId, String executionId) {
        return responseFor(testCaseResultService.findTestCaseResults(jobId, executionId))
            .withSelfRel(TestSessionResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_VALUE, executionId))
            .build();
    }
}

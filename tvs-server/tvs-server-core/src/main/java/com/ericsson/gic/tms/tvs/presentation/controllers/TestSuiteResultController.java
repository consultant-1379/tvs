package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.CollectionMetadataService;
import com.ericsson.gic.tms.tvs.application.services.QueryService;
import com.ericsson.gic.tms.tvs.application.services.SortService;
import com.ericsson.gic.tms.tvs.application.services.TestSuiteResultService;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import com.ericsson.gic.tms.tvs.presentation.resources.TestSuiteResultResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;

@Controller
public class TestSuiteResultController extends AbstractJsonApiCapableController implements TestSuiteResultResource {

    @Autowired
    private TestSuiteResultService testSuiteResultService;

    @Autowired
    private CollectionMetadataService collectionMetadataService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private SortService sortService;

    @Override
    public DocumentList<TestSuiteResultBean> getList(String jobId, String executionId, int page, int size,
                                                     String orderBy, SortingMode orderMode, String query) {
        //TODO: MongoDb does not have ignoreCase sorting :(
        PageRequest pageRequest = new PageRequest(page - 1, size, sortService.getSort(orderMode.toString(), orderBy));

        Query parsedQuery = queryService.createQuery(query);

        Page<TestSuiteResultBean> testSessions =
                testSuiteResultService.getPaginatedTestSuites(jobId, executionId, pageRequest, parsedQuery);

        return responseFor(testSessions.getContent())
                .withSelfRel(TestSuiteResultResource.class)
                .withPagination(TestSuiteResultResource.class, testSessions, pageRequest)
                .withMeta(COLUMNS, collectionMetadataService.getColumns(TEST_SUITE_RESULT.getName()))
                .build();
    }

    @Override
    public Document<TestSuiteResultBean> getTestSuiteResult(String jobId, String executionId, String testSuiteName) {
        return responseFor(testSuiteResultService.getTestSuiteResult(jobId, executionId, testSuiteName))
            .withSelfRel(TestSuiteResultResource.class, TEST_SUITE)
            .withMeta("children", testSuiteResultService.getTestSuiteChildrenIds(jobId, executionId, testSuiteName))
            .build();
    }

    @Override
    public Document<TestSuiteResultBean> aggregateTestSuiteResult(String jobId, String executionId,
                                                                  String testSuiteName) {
        return responseFor(testSuiteResultService.aggregateTestSuite(jobId, executionId, testSuiteName))
            .withSelfRel(TestSuiteResultResource.class, TEST_SUITE_AGGREGATION)
            .build();
    }
}

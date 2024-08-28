package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.application.services.ResultPath;
import com.ericsson.gic.tms.tvs.application.services.TestCaseResultService;
import com.ericsson.gic.tms.tvs.application.services.TestSuiteResultService;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.resources.TestCaseResultInsertResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.verifyFound;
import static com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode.ASCENDING;

@Controller
public class TestCaseResultInsertController
    extends AbstractJsonApiCapableController
    implements TestCaseResultInsertResource {

    @Autowired
    private TestCaseResultService testCaseResultService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TestSuiteResultService testSuiteResultService;

    @Override
    public Document<TestCaseResultBean> updateTestCaseResult(String contextId, String jobName, String executionId,
                                                             String suiteName, String testCaseResultId,
                                                             TestCaseResultBean testCaseResultBean) {
        Job job = verifyFound(jobRepository.findByNameAndContextId(jobName, contextId));
        TestSuiteResult testSuiteResult =
            verifyFound(testSuiteResultService.getTestSuiteResultEntity(job.getUid(), executionId, suiteName));

        ResultPath resultPath = new ResultPath()
            .withJob(contextId, job.getUid())
            .withTestSession(executionId, testSuiteResult.getTestSessionId())
            .withTestSuiteResult(testSuiteResult.getName(), testSuiteResult.getId());

        TestCaseResultBean resource = testCaseResultService.updateTestCaseResult(
            resultPath,
            testCaseResultId,
            testCaseResultBean
        );

        testSuiteResultService.aggregateTestSuite(job.getUid(), executionId, suiteName, ASCENDING);

        return responseFor(resource)
            .withSelfRel(TestCaseResultInsertResource.class)
            .build();
    }
}

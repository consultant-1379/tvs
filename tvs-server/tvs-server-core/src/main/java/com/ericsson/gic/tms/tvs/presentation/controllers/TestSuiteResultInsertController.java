package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.application.services.ResultPath;
import com.ericsson.gic.tms.tvs.application.services.TestSuiteResultService;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import com.ericsson.gic.tms.tvs.presentation.resources.TestSuiteResultInsertResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.verifyFound;
import static com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode.ASCENDING;

@Controller
public class TestSuiteResultInsertController extends AbstractJsonApiCapableController
    implements TestSuiteResultInsertResource {

    @Autowired
    private TestSuiteResultService testSuiteResultService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Override
    public Document<TestSuiteResultBean> updateTestSuiteResult(String contextId, String jobName, String executionId,
                                                               String testSuiteName, TestSuiteResultBean bean) {
        Job job = verifyFound(jobRepository.findByNameAndContextId(jobName, contextId));
        TestSession session = verifyFound(testSessionRepository.findByJobIdAndExecutionId(job.getUid(), executionId));

        ResultPath resultPath = new ResultPath()
            .withJob(contextId, session.getJobId())
            .withTestSession(executionId, session.getId());

        TestSuiteResultBean updated = testSuiteResultService.updateTestSuiteResult(
            resultPath, testSuiteName, bean);
        TestSuiteResultBean aggregated =
            testSuiteResultService.aggregateTestSuite(job.getUid(), executionId, testSuiteName, ASCENDING);

        updated.setStatistics(aggregated.getStatistics());
        updated.setPassRate(aggregated.getPassRate());

        return responseFor(updated)
            .withSelfRel(TestSuiteResultInsertResource.class)
            .build();
    }

}

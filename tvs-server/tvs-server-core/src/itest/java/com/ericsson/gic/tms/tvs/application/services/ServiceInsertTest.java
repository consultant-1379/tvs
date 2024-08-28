package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSuiteResultRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestVerdictBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.*;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static org.assertj.core.api.Assertions.*;

public class ServiceInsertTest extends AbstractIntegrationTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private TestSuiteResultService testSuiteResultService;

    @Autowired
    private TestCaseResultService testCaseResultService;

    @Autowired
    private TestCaseAggregationService testCaseAggregationService;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Autowired
    private AggregationRequirementService aggregationRequirementService;

    @Test
    public void testUpdateJobWithNestedResults() throws Exception {
        JobBean job = job(
            testSession(
                testSuiteResult(
                    testCaseResult(),
                    testCaseResult()
                ),
                testSuiteResult()
            ),
            testSession()
        );

        String contextId = uniqueString();
        JobBean createdJob = jobService.updateJob(contextId, job.getName(), job);

        assertThat(createdJob.getId()).isNotEmpty();
        assertThat(createdJob.getTestSessions()).hasSize(2);

        TestSessionBean testSession1 = createdJob.getTestSessions().get(0);
        assertThat(testSession1.getTestSuites()).hasSize(2);

        TestSessionBean testSession2 = createdJob.getTestSessions().get(1);
        assertThat(testSession2.getTestSuites()).hasSize(0);

        TestSuiteResultBean testSuiteResult1 = testSession1.getTestSuites().get(0);
        assertThat(testSuiteResult1.getTestCaseResults()).hasSize(2);

        TestSuiteResultBean testSuiteResult2 = testSession1.getTestSuites().get(1);
        assertThat(testSuiteResult2.getTestCaseResults()).hasSize(0);

        String jobId = createdJob.getId();
        String executionId = testSession1.getId();
        String testSuiteName = testSuiteResult1.getId();
        String testCaseId = testSuiteResult1.getTestCaseResults().get(0).getId();

        // Verify found
        jobService.getJob(jobId);
        testSessionService.getTestSession(jobId, executionId);
        testSuiteResultService.getTestSuiteResult(jobId, executionId, testSuiteName);
        testCaseResultService.getTestCaseResult(jobId, executionId, testSuiteName, testCaseId);

        // Verify saved path
        String testSessionId = verifyFound(
            testSessionRepository.findByExecutionIdAndJobId(executionId, jobId)).getId();
        String testSuiteResultId = verifyFound(
            testSuiteResultRepository.findByTestSessionIdAndName(testSessionId, testSuiteName)).getId();
        TestCaseResult testCaseResult = verifyFound(
            testCaseResultRepository.findByTestCaseIdAndAndTestSuiteResultId(testCaseId, testSuiteResultId));
        assertThat(testCaseResult.getJobId()).isEqualTo(jobId);
        assertThat(testCaseResult.getExecutionId()).isEqualTo(executionId);
        assertThat(testCaseResult.getTestSuiteName()).isEqualTo(testSuiteName);
        assertThat(testCaseResult.getTestCaseId()).isEqualTo(testCaseId);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testSaveDifferentJobsWithSameTestSession() throws Exception {
        String contextId = uniqueString();
        String testSessionId = uniqueString();
        String testSuiteResultId1 = uniqueString();
        String testSuiteResultId2 = uniqueString();
        String testCaseId = uniqueString();

        saveAndGetTestCaseId(contextId, testSessionId, testSuiteResultId1, testCaseId);
        saveAndGetTestCaseId(contextId, testSessionId, testSuiteResultId2, testCaseId);

        TestVerdictBean statistics = testCaseAggregationService.getTestCaseResultStatistics(testCaseId);
        assertThat(statistics.getExecutionCount()).isEqualTo(2);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testSaveDifferentJobsWithSameTestSuite() throws Exception {
        String contextId = uniqueString();
        String testSessionId1 = uniqueString();
        String testSessionId2 = uniqueString();
        String testSuiteResultId = uniqueString();
        String testCaseId = uniqueString();

        saveAndGetTestCaseId(contextId, testSessionId1, testSuiteResultId, testCaseId);
        saveAndGetTestCaseId(contextId, testSessionId2, testSuiteResultId, testCaseId);

        TestVerdictBean statistics = testCaseAggregationService.getTestCaseResultStatistics(testCaseId);
        assertThat(statistics.getExecutionCount()).isEqualTo(2);
    }

    private void saveAndGetTestCaseId(String contextId,
                                      String testSessionId,
                                      String testSuiteResultId,
                                      String testCaseId) throws Exception {
        JobBean job = job(testSession(testSessionId,
            testSuiteResult(testSuiteResultId, testCaseResult(testCaseId))));
        job = jobService.updateJob(contextId, job.getName(), job);

        List<String> testCaseIds = testSuiteResultService.getTestSuiteChildrenIds(
            job.getId(),
            testSessionId,
            testSuiteResultId
        );
        assertThat(testCaseIds)
            .hasSize(1)
            .containsExactly(testCaseId);
    }

}

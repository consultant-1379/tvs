package com.ericsson.gic.tms.tvs.migrations;

import com.ericsson.gic.tms.TvsApplication;
import com.ericsson.gic.tms.tvs.application.services.ResultPath;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSuiteResultRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TvsApplication.class})
public class TestCaseResultParentsMigration {

    private final Logger logger = LoggerFactory.getLogger(TestCaseResultParentsMigration.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Test
    public void guard() {
        throw new RuntimeException("This test class should be annotated with @Ignore");
    }

    @Test
    public void reindexAllJobs() {
        ResultPath resultPath = new ResultPath();
        Iterable<Job> jobs = jobRepository.findAll();
        long total = jobRepository.count();
        int processed = 0;
        for (Job job : jobs) {
            String jobId = job.getUid();
            ResultPath childResultPath = resultPath.withJob(job.getContextId(), jobId);
            reindexTestSessions(childResultPath);
            logger.info("Updated results in job {} ({}/{})", jobId, ++processed, total);
        }
    }

    @Test
    public void reindexSingleJob() {
        String jobId = "a9f31797-30a5-4358-bf50-8826457eec14";
        Job job = jobRepository.findOne(jobId);
        ResultPath childResultPath = new ResultPath().withJob(job.getContextId(), jobId);
        reindexTestSessions(childResultPath);
        logger.info("Updated results in job {}", jobId);
    }

    private void reindexTestSessions(ResultPath resultPath) {
        testSessionRepository.findByJobId(resultPath.getJobId())
            .forEach(testSession -> {
                ResultPath childResultPath =
                    resultPath.withTestSession(testSession.getExecutionId(), testSession.getId());
                reindexTestSuiteResults(childResultPath);
            });
    }

    private void reindexTestSuiteResults(ResultPath resultPath) {
        testSuiteResultRepository.findByTestSessionId(resultPath.getTestSessionId())
            .forEach(testSuiteResult -> {
                ResultPath childResultPath =
                    resultPath.withTestSuiteResult(testSuiteResult.getName(), testSuiteResult.getId());
                reindexTestCaseResults(childResultPath);
            });
    }

    private void reindexTestCaseResults(ResultPath resultPath) {
        testCaseResultRepository.findByTestSuiteResultId(resultPath.getTestSuiteResultId())
            .forEach(testCaseResult -> {
                testCaseResult.setContextId(resultPath.getContextId());
                testCaseResult.setJobId(resultPath.getJobId());
                testCaseResult.setExecutionId(resultPath.getExecutionId());
                testCaseResult.setTestSuiteName(resultPath.getTestSuiteName());
                testCaseResultRepository.save(testCaseResult);
            });
    }

}

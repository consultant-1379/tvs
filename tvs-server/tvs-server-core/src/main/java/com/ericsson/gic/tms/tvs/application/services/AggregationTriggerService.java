package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AggregationTriggerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregationTriggerService.class);

    @Autowired
    private TestSuiteResultService testSuiteResultService;

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private JobService jobService;

    @Autowired
    private AggregationRequirementService aggregationRequirementService;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private LocalDateTime lastRun;

    public void trigger() {
        if (isRunning.compareAndSet(false, true)) {
            lastRun = LocalDateTime.now();
            try {
                retriggerJobs();
                retriggerRequirements();
            } finally {
                isRunning.set(false);
            }
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public LocalDateTime getLastRun() {
        return lastRun;
    }

    @Scheduled(cron = "${schedule.aggregate.requirement.cron}")
    private void retriggerRequirements() {
        LOGGER.info("trigger US requirement aggregation");
        aggregationRequirementService.aggregateUserStories();
        LOGGER.info("trigger EPIC requirement aggregation");
        aggregationRequirementService.aggregateEpics();
        LOGGER.info("trigger MR requirement aggregation");
        aggregationRequirementService.aggregateMainRequirements();
    }

    private void retriggerJobs() {
        LOGGER.info("trigger calculation of job execution aggregation data");
        List<String> jobs = getAllJobIds();
        int processed = 0;
        for (String jobId : jobs) {
            LOGGER.info("aggregating results in job {} ({}/{})", jobId, ++processed, jobs.size());
            retriggerJob(jobId);
        }
        LOGGER.info("DONE. All job execution aggregation data have been calculated");
    }

    private List<String> getAllJobIds() {
        ArrayList<String> jobs = new ArrayList<>();
        jobService.getAllJobs().forEach(job -> jobs.add(job.getUid()));
        return jobs;
    }

    private void retriggerJob(String jobId) {
        List<TestSession> testSessions = testSessionService.getAllTestSessions(jobId);
        testSessions
            .forEach((testSession) -> retriggerTestSession(jobId, testSession.getExecutionId(), testSession.getId()));

        try {
            jobService.aggregateJob(jobId);
        } catch (NotFoundException e) {
            // Happens in case when there are no child test sessions. Should be ignored
        }
    }

    private void retriggerTestSession(String jobId, String executionId, String testSessionId) {
        testSuiteResultService.getAllTestSuites(testSessionId).forEach(testSuiteResult ->
            retriggerTestSuite(jobId, executionId, testSuiteResult.getName()));

        try {
            testSessionService.aggregateTestSession(jobId, executionId);
        } catch (NotFoundException e) {
            // Happens in case when there are no child test suites. Should be ignored
        }
    }

    private void retriggerTestSuite(String jobId, String executionId, String testSuiteName) {
        try {
            testSuiteResultService.aggregateTestSuite(jobId, executionId, testSuiteName);
        } catch (NotFoundException e) {
            // Happens in case when there are no child test cases. Should be ignored
        }
    }
}

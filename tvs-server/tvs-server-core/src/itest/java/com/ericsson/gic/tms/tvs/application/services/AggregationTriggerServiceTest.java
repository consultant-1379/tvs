package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.AbstractDbunitTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.FAILED;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.PASSED;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.job;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.testCaseResult;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.testSession;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.testSuiteResult;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.uniqueString;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AggregationTriggerServiceTest extends AbstractDbunitTest {

    @Autowired
    private AggregationTriggerService aggregationTriggerService;

    @Autowired
    private JobService jobService;

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private TestSuiteResultService testSuiteResultService;

    @Test
    public void testAggregation() {
        prepareData();

        Job job = newArrayList(jobService.getAllJobs()).get(0);
        assertThat(job.getLastTestSessionTestSuiteCount()).isEqualTo(1);

        List<TestSession> allTestSessions = testSessionService.getAllTestSessions(job.getUid());
        TestSession testSession = allTestSessions.get(0);
        assertThat(testSession.getTestSuiteCount()).isEqualTo(1);

        List<TestSuiteResult> allTestSuites = testSuiteResultService.getAllTestSuites(testSession.getId());
        assertThat(allTestSuites.get(0).getPassRate()).isEqualTo(50);
    }


    private void prepareData() {
        String executionId1 = uniqueString();
        String executionId2 = uniqueString();

        JobBean job = job(
            testSession(executionId1,
                testSuiteResult(
                    testCaseResult(uniqueString(), PASSED),
                    testCaseResult(uniqueString(), FAILED)
                )
            ),
            testSession(executionId2,
                testSuiteResult(
                    testCaseResult(uniqueString(), PASSED),
                    testCaseResult(uniqueString(), FAILED)
                )
            )
        );

        String contextId = uniqueString();
        jobService.updateJob(contextId, job.getName(), job);
    }
}

package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class JobServiceTest extends AbstractIntegrationTest {

    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String OLD_CONTEXT_ID = "systemId-1";
    private static final String NEW_CONTEXT_ID = "systemId-2";

    @Autowired
    private JobService jobService;

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Test
    public void moveJobToAnotherContext() {
        JobBean job = jobService.getJob(JOB_ID);
        assertThat(job.getContext()).isEqualTo(OLD_CONTEXT_ID);

        List<TestCaseResult> testCaseResults = testCaseResultRepository.findByJobId(JOB_ID);
        assertThat(testCaseResults).extracting(TestCaseResult::getContextId)
            .containsOnly(OLD_CONTEXT_ID);

        jobService.moveJobToContext(JOB_ID, NEW_CONTEXT_ID);

        job = jobService.getJob(JOB_ID);
        assertThat(job.getContext()).isEqualTo(NEW_CONTEXT_ID);

        testCaseResults = testCaseResultRepository.findByJobId(JOB_ID);
        assertThat(testCaseResults).extracting(TestCaseResult::getContextId)
            .containsOnly(NEW_CONTEXT_ID);
    }

    @Test
    public void updateJob() {
        JobBean job = jobService.getJob(JOB_ID);
        assertThat(job.getContext()).isEqualTo(OLD_CONTEXT_ID);

        List<TestCaseResult> testCaseResults = testCaseResultRepository.findByJobId(JOB_ID);
        assertThat(testCaseResults).extracting(TestCaseResult::getContextId)
            .containsOnly(OLD_CONTEXT_ID);

        job.setContext(NEW_CONTEXT_ID);
        jobService.updateJob(OLD_CONTEXT_ID, job.getName(), job);

        job = jobService.getJob(JOB_ID);
        assertThat(job.getContext()).isEqualTo(NEW_CONTEXT_ID);

        testCaseResults = testCaseResultRepository.findByJobId(JOB_ID);
        assertThat(testCaseResults).extracting(TestCaseResult::getContextId)
            .containsOnly(NEW_CONTEXT_ID);
    }
}

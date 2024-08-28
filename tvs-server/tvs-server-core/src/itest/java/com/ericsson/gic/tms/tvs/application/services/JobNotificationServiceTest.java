package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.infrastructure.mail.EmailServiceStub;
import com.ericsson.gic.tms.tvs.presentation.builders.RuleBuilder;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RecipientBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RuleBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.JENKINS_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         05/04/2017
 */
public class JobNotificationServiceTest extends AbstractIntegrationTest {

    private static final String JOB_NAME = "RFA_Job";
    private static final String EMAIL = "test@ericsson.com";

    @Autowired
    private JobService jobService;

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    @InjectMocks
    private JobNotificationService jobNotificationService;

    @Spy
    private EmailServiceStub emailService;

    private TestSessionBean testSession;
    private JobBean job;
    private String contextId;

    @Captor
    private ArgumentCaptor<List<String>> recipientsCaptor;

    @Captor
    private ArgumentCaptor<String> subjectCaptor;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        contextId = uniqueString();

        job = new JobBean();
        job.setName(JOB_NAME);
        job = jobService.updateJob(contextId, job.getName(), job);

        testSession = testSessionWithAdditionalData(
            "12.35",
            null,
            "1.3",
            testSuiteResult(
                testCaseResult(),
                testCaseResult("id", "BROKEN")
            )
        );
        testSession.addAdditionalFields(JENKINS_JOB_NAME, JOB_NAME);

        ResultPath resultPath = new ResultPath().withJob(contextId, job.getId());
        testSession = testSessionService.updateTestSession(resultPath, uniqueString(), testSession);
    }

    @Test
    public void verifyJobExecution() throws Exception {

        RuleBean rule = new RuleBuilder()
            .withField("passRate")
            .withFieldType("number")
            .withOperation("<")
            .withValue("60")
            .build();

        jobNotificationService.updateRule(job.getId(), null, rule);
        jobNotificationService.addRecipient(job.getId(),
            new RecipientBean(EMAIL));

        jobNotificationService.verifyJobExecution(job.getId(), testSession.getExecutionId());

        verify(emailService).sendEmail(recipientsCaptor.capture(), subjectCaptor.capture(), anyString());

        assertThat(recipientsCaptor.getValue()).containsOnly(EMAIL);
        assertThat(subjectCaptor.getValue()).contains(JOB_NAME, "passRate", "over threshold");
    }

    @After
    public void tearDown() throws Exception {

    }

}

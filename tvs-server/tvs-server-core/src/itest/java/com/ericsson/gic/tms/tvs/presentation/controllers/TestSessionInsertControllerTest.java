package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Meta;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.application.services.JobService;
import com.ericsson.gic.tms.tvs.application.services.TestSuiteResultService;
import com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory;
import com.ericsson.gic.tms.tvs.domain.model.verdict.FieldMetadata;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Statistics;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

public class TestSessionInsertControllerTest extends AbstractIntegrationTest {

    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String CONTEXT_ID = "systemId-1";
    private static final String JOB_NAME = "X_SCHEDULER_1";
    private static final String EXECUTION_ID = "session_id_1";
    private static final String NEW_EXECUTION_ID = "unexisting_session_id";
    private static final String NEW_JOB_NAME = "NEW_JOB_NAME";
    private static final String ANOTHER_EXECUTION_ID = "ex_id";

    private static final String ID = "id";


    @Autowired
    private TestSessionInsertController testSessionInsertController;

    @Autowired
    private TestSessionController testSessionController;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Autowired
    private TestSuiteResultService suiteResultService;

    @Autowired
    private JobService jobService;

    private TestSessionBean testSessionBean;

    @Before
    public void setUp() {
        testSessionController.setJsonApiUtil(mockedJsonApiUtil);
        testSessionInsertController.setJsonApiUtil(mockedJsonApiUtil);

        testSessionBean = new TestSessionBean();
        testSessionBean.setUri("http://somelink.com");
        ExecutionTimeBean timeBean = new ExecutionTimeBean();
        timeBean.setStartDate(new Date());
        testSessionBean.setTime(timeBean);
        testSessionBean.setTestSuites(emptyList());
    }

    @Test
    public void testSessionModifiedDate() {
        String testSessionId = testSessionInsertController.updateTestSession(CONTEXT_ID, JOB_NAME,
            NEW_EXECUTION_ID, testSessionBean).unwrap().getId();
        TestSessionBean inserted = testSessionController.getTestSession(JOB_ID, testSessionId).unwrap();

        testSessionId = testSessionInsertController.updateTestSession(CONTEXT_ID, JOB_NAME,
            NEW_EXECUTION_ID, testSessionBean).unwrap().getId();
        TestSessionBean updated = testSessionController.getTestSession(JOB_ID, testSessionId).unwrap();

        assertThat(updated.getId()).isEqualTo(inserted.getId());
        assertThat(updated.getModifiedDate()).isAfter(inserted.getModifiedDate());
    }

    @Test
    public void createNewTestSession() {
        TestSessionBean testSession = testSessionInsertController.updateTestSession(CONTEXT_ID, JOB_NAME,
            NEW_EXECUTION_ID, testSessionBean).unwrap();

        assertThat(testSession)
            .isEqualToComparingOnlyGivenFields(testSessionBean, "uri");
        assertThat(testSession.getTime())
            .isEqualToIgnoringGivenFields(testSessionBean.getTime(), "duration");
        assertThat(testSession.getCreatedDate())
            .isNotNull();
    }

    @Test
    public void metadataIsPopulated() {
        String generatedKey = UUID.randomUUID().toString();
        testSessionBean.addAdditionalFields(generatedKey, "something");

        testSessionInsertController.updateTestSession(CONTEXT_ID, NEW_JOB_NAME, EXECUTION_ID, testSessionBean);

        DocumentList<TestSessionBean> testSessionDocument = testSessionController
            .getList(CONTEXT_ID, null, null, 1, 20, ID, SortingMode.ASC, null);

        assertThat(testSessionDocument).isNotNull();
        assertThat(testSessionDocument.getMeta()).isNotNull();
        Collection<FieldMetadata> columns = (Collection<FieldMetadata>) testSessionDocument.getMeta().get(Meta.COLUMNS);
        assertThat(columns).isNotNull();
        assertThat(columns).extracting(FieldMetadata::getField).contains(generatedKey);
    }

    @Test
    public void additionalFieldsArePersisted() {
        testSessionBean.addAdditionalFields(DROP_NAME, "16.2");
        testSessionBean.addAdditionalFields(ISO_ARTIFACT_ID, "ERICenm_CXP903938");
        testSessionBean.addAdditionalFields(ISO_VERSION, "1.18.41");
        testSessionBean.addAdditionalFields(JENKINS_JOB_NAME, "RFA_JOB");
        testSessionInsertController.updateTestSession(CONTEXT_ID, JOB_NAME,
            NEW_EXECUTION_ID, testSessionBean).unwrap();

        TestSessionBean testSession = testSessionController.getTestSession(JOB_ID, NEW_EXECUTION_ID).unwrap();

        assertThat(testSession).as("Updated Test Session")
            .isNotNull()
            .isEqualToIgnoringGivenFields(testSessionBean, "executionId", "time", "testSuites",
                "modifiedDate", "createdDate");
        assertThat(testSession.getId())
            .as("Test Session ID")
            .isNotEmpty();
        assertThat(testSession.getModifiedDate())
            .as("Test Session Modified Date")
            .isNotNull();

        assertThat(testSession.getCreatedDate())
            .as("Test Session Created Date")
            .isNotNull();
    }

    @Test
    public void nullAdditionalFieldsAreIgnored() {

        Map<String, Object> additionalFields = new HashMap<>();
        additionalFields.put(ISO_VERSION, null);

        testSessionBean.addAdditionalFields(additionalFields);
        testSessionBean.addAdditionalFields(DROP_NAME, null);
        testSessionBean.addAdditionalFields(JENKINS_JOB_NAME, "RFA_JOB");

        testSessionInsertController.updateTestSession(CONTEXT_ID, JOB_NAME,
            NEW_EXECUTION_ID, testSessionBean).unwrap();

        TestSessionBean testSession = testSessionController.getTestSession(JOB_ID, NEW_EXECUTION_ID).unwrap();

        assertThat(testSession.getAdditionalFields()).doesNotContainKeys(ISO_VERSION, DROP_NAME);
        assertThat(testSession.getAdditionalFields()).contains(entry(JENKINS_JOB_NAME, "RFA_JOB"));
    }

    @Test
    public void updateExistingTestSession() {
        TestSessionBean testSession = testSessionController.getTestSession(JOB_ID, EXECUTION_ID).unwrap();

        testSession.getTime().setStopDate(new Date());
        testSession.getTime().setDuration(null);
        testSession.setUri("http://otheruri.com");
        testSession.setCreatedDate(null);
        testSession.setId(null);
        testSession.setTestSuites(emptyList());

        TestSessionBean updatedTestSession = testSessionInsertController
            .updateTestSession(CONTEXT_ID, JOB_NAME, EXECUTION_ID, testSession).unwrap();

        assertThat(updatedTestSession)
            .isEqualToIgnoringGivenFields(testSession, "executionId", "time", "createdDate", "modifiedDate");
        assertThat(updatedTestSession.getTime())
            .isEqualToIgnoringGivenFields(testSession.getTime(), "duration");
    }

    @Test
    public void ignoreTestSession() {
        final String executionId = uniqueString();
        TestSessionBean testSession = testSession(executionId, testSuiteResult(testCaseResult()));
        testSession = testSessionInsertController
            .updateTestSession(CONTEXT_ID, JOB_NAME, executionId, testSession).unwrap();
        assertThat(testSession.isIgnored()).isFalse();
        List<TestCaseResult> testCases = testCaseResultRepository.findByJobExecutionId(executionId);
        assertThat(testCases).hasSize(1);

        TestSessionBean bean = new TestSessionBean();
        bean.setTime(testSession.getTime());
        bean.setExecutionId(testSession.getExecutionId());
        bean.setIgnored(true);

        testSessionInsertController
            .updateTestSession(CONTEXT_ID, JOB_NAME, executionId, bean).unwrap();

        TestSessionBean updatedTestSession = testSessionController.getTestSession(JOB_ID, executionId).unwrap();
        assertThat(updatedTestSession.isIgnored()).isTrue();
        assertThat(updatedTestSession)
            .isEqualToIgnoringGivenFields(testSession, "testSuites", "time", "modifiedDate", "ignored");
        assertThat(updatedTestSession.getTime())
            .isEqualToComparingFieldByField(testSession.getTime());

        testCases = testCaseResultRepository.findByJobExecutionId(executionId);
        assertThat(testCases).isEmpty();
    }

    @Test
    public void doubleInsertTest() {
        testSessionInsertController.updateTestSession(CONTEXT_ID, NEW_JOB_NAME, EXECUTION_ID, testSessionBean);
        long jobAmount = jobRepository.count();
        long sessionAmount = testSessionRepository.count();

        Job job = jobRepository.findByNameAndContextId(NEW_JOB_NAME, CONTEXT_ID);
        assertThat(job).isNotNull();
        String uid = job.getUid();
        assertThat(uid).isNotNull();

        testSessionInsertController.updateTestSession(CONTEXT_ID, NEW_JOB_NAME, ANOTHER_EXECUTION_ID, testSessionBean);

        job = jobRepository.findByNameAndContextId(NEW_JOB_NAME, CONTEXT_ID);
        assertThat(job).isNotNull();
        assertThat(job.getUid()).isNotNull();
        assertThat(job.getUid()).isEqualTo(uid);
        assertThat(jobRepository.count()).isEqualTo(jobAmount);
        assertThat(testSessionRepository.count()).isEqualTo(sessionAmount + 1);
    }

    @Test
    public void updateInvalidTestCaseBean() {
        testSessionBean.getTime().setStartDate(null); // reset for constrain violation
        assertThatThrownBy(() -> testSessionInsertController.updateTestSession(CONTEXT_ID, NEW_JOB_NAME,
            ANOTHER_EXECUTION_ID, testSessionBean))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void aggregateTestSession() {

        JobBean job = job();
        job = jobService.updateJob(CONTEXT_ID, job.getName(), job);
        assertThat(job.getLastTestSessionTestSuiteCount()).isEqualTo(0);

        TestSessionBean testSession = testSession(
            testSuiteResult(
                testCaseResult(uniqueString(), TvsBeanFactory.PASSED),
                testCaseResult(uniqueString(), FAILED)
            )
        );

        testSessionInsertController.updateTestSession(CONTEXT_ID, job.getName(), testSession.getExecutionId(),
            testSession);

        job = jobService.getJob(job.getId());
        assertThat(job.getLastTestSessionTestSuiteCount()).isEqualTo(1);
        assertThat(job.getLastTestSessionTestCaseCount()).isEqualTo(2);

        List<TestSession> allTestSessions = testSessionRepository.findByJobId(job.getId());
        TestSession savedSession = allTestSessions.get(0);
        assertThat(savedSession.getTestSuiteCount()).isEqualTo(1);
        assertThat(savedSession.getTestCaseCount()).isEqualTo(2);
        assertThat(savedSession.getPassRate()).isEqualTo(50);
        assertThat(savedSession.getLastExecutionDate()).isNotNull();

        TestSuiteResult suite = suiteResultService.getAllTestSuites(savedSession.getId()).get(0);
        assertThat(suite.getPassRate()).isEqualTo(50);

        Statistics statistics = suite.getStatistics();
        assertThat(statistics.getPassed()).isEqualTo(1);
        assertThat(statistics.getFailed()).isEqualTo(1);
        assertThat(statistics.getTotal()).isEqualTo(2);
    }
}

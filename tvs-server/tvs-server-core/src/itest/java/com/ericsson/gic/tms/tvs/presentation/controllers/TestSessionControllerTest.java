package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.converters.DateConverter;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Meta;
import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.application.services.JobService;
import com.ericsson.gic.tms.tvs.application.services.ResultPath;
import com.ericsson.gic.tms.tvs.application.services.TestSessionService;
import com.ericsson.gic.tms.tvs.application.services.TestSuiteResultService;
import com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.DESC;
import static com.ericsson.gic.tms.presentation.validation.NotFoundException.verifyFound;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.PASSED;
import static com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode.*;
import static java.util.Collections.*;
import static javax.ws.rs.core.HttpHeaders.*;
import static org.assertj.core.api.Assertions.*;

public class TestSessionControllerTest extends AbstractIntegrationTest {

    private static final String CONTEXT_ID = "systemId-2";
    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String INVALID_JOB_ID = "invalid-job-id";
    private static final String EXECUTION_ID = "session_id_1";
    private static final String INVALID_EXECUTION_ID = "invalid_execution_id";
    private static final String SUITE_NAME = "testsuite_id_1";

    private static final String START_TIME = "time.startDate";
    private static final String ISO_VERSION_PADDED = "ISO_VERSION_PADDED";
    private static final String ISO_VERSION = "ISO_VERSION";

    @Autowired
    private TestSessionController controller;

    @Autowired
    private JobService jobService;

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private TestSuiteResultService suiteResultService;

    @Autowired
    private TestSessionRepository testSessionRepository;

    private DateConverter dateConverter = new DateConverter(ZoneId.of("UTC"));

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);
    }

    @Test
    public void getTestSession() {
        TestSessionBean expected = new TestSessionBean();
        expected.setId(EXECUTION_ID);
        expected.setResultCode(PASSED.name());
        expected.setUri("https://fem114-eiffel004.lmera.ericsson.se:8443/jenkins/job/5294_TAF_RFA_Loop/50/");
        expected.setLastExecutionDate(toDate("2015-12-11T14:01:57"));
        expected.setPassRate(50);
        expected.setTestCaseCount(2);
        expected.setTestSuiteCount(1);
        expected.addAdditionalFields(DROP_NAME, "1.2");
        expected.addAdditionalFields(ISO_ARTIFACT_ID, "ERICenm_CXP903938");
        expected.addAdditionalFields(ISO_VERSION, "1.18.40");
        expected.addAdditionalFields(ISO_VERSION_PADDED, "000100180040");
        expected.addAdditionalFields(JENKINS_JOB_NAME, "RFA_JOB");

        TestSessionBean queried = controller.getTestSession(JOB_ID, EXECUTION_ID).unwrap();

        assertThat(queried)
            .as("Job Session Fields")
            .isEqualToIgnoringGivenFields(expected, "time", "testSuites", "createdDate");

        ExecutionTimeBean executionTime = new ExecutionTimeBean();
        executionTime.setStartDate(toDate("2015-12-11T14:01:57"));
        executionTime.setStopDate(toDate("2015-12-11T14:36:57"));
        executionTime.setDuration(2100000L);

        assertThat(queried.getTime())
            .isEqualToComparingFieldByField(executionTime);
    }

    @Test
    public void testGetTestSessionPage() {
        List<TestSessionBean> queried = controller.getList(JOB_ID, null, null,
                1, 20, START_TIME, DESC, null).unwrap();

        assertThat(queried)
            .as("Test Sessions Response")
            .hasSize(4)
            .doesNotContainNull()
            .doesNotHaveDuplicates();

        assertThat(queried).extracting(TestSessionBean::getTime).isNotNull();
        assertThat(queried)
            .as("Test Session Sort by Desc")
            .isSortedAccordingTo(
                (TestSessionBean session1, TestSessionBean session2) ->
                    session2.getTime().getStartDate().compareTo(session1.getTime().getStartDate()));
    }

    @Test
    public void testGetTestSessionPageSortedByISOVersion() {
        List<TestSessionBean> queried = controller.getList(JOB_ID, null, null,
                1, 20, ISO_VERSION_PADDED, DESC, null).unwrap();

        assertThat(queried)
                .as("Test Sessions Response")
                .hasSize(4)
                .doesNotContainNull()
                .doesNotHaveDuplicates();

        assertThat(queried.get(0).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.42");
        assertThat(queried.get(1).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.41");
        assertThat(queried.get(2).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.40");
        assertThat(queried.get(3).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.4");
    }

    @Test
    public void getChildrenInMeta() {
        Document<TestSessionBean> response = controller.getTestSession(JOB_ID, EXECUTION_ID);

        @SuppressWarnings("unchecked")
        List<String> children = (List<String>) response.getMeta().get(Meta.CHILDREN);
        assertThat(children)
            .containsOnly(SUITE_NAME);
    }

    @Test
    public void aggregateTestSession() {
        TestSession session = verifyFound(testSessionRepository.findByExecutionIdAndJobId(EXECUTION_ID, JOB_ID));
        ResultPath resultPath = new ResultPath()
            .withJob(CONTEXT_ID, session.getJobId())
            .withTestSession(EXECUTION_ID, session.getId());

        ExecutionTimeBean time = new ExecutionTimeBean();
        time.setStartDate(new Date());
        time.setStopDate(new Date());
        StatisticsBean statistics = new StatisticsBean();
        statistics.setTotal(10);
        statistics.setPassed(2);

        TestSuiteResultBean bean = new TestSuiteResultBean();
        bean.setTestCaseResults(emptyList());
        bean.setPassRate(20);
        bean.setTime(time);
        bean.setStatistics(statistics);

        suiteResultService.updateTestSuiteResult(resultPath, "new suite", bean);

        TestSessionBean expected = controller.getTestSession(JOB_ID, EXECUTION_ID).unwrap();
        expected.setLastExecutionDate(time.getStartDate());
        expected.setTestSuiteCount(2);
        expected.setTestCaseCount(12);
        expected.setPassRate(25);

        TestSessionBean testSessionBean = controller.aggregateTestSession(JOB_ID, EXECUTION_ID, SELF).unwrap();

        assertThat(testSessionBean)
            .isEqualToIgnoringGivenFields(expected, "time", "modifiedDate");

        assertThat(testSessionBean.getTime())
            .isEqualToComparingFieldByField(expected.getTime());
    }

    @Test
    public void aggregateNewTestSession() {

        JobBean job = prepareData();

        assertThat(job.getLastTestSessionTestSuiteCount()).isEqualTo(1);

        List<TestSession> allTestSessions = testSessionService.getAllTestSessions(job.getId());
        assertThat(allTestSessions.get(0).getTestSuiteCount()).isEqualTo(1);

        TestSession testSession = allTestSessions.get(0);
        List<TestSuiteResult> allTestSuites = suiteResultService.getAllTestSuites(testSession.getId());
        assertThat(allTestSuites.get(0).getPassRate()).isEqualTo(50);
    }

    private JobBean prepareData() {

        JobBean job = job(
            testSession(
                testSuiteResult(
                    testCaseResult(uniqueString(), TvsBeanFactory.PASSED),
                    testCaseResult(uniqueString(), FAILED)
                )
            )
        );

        return jobService.updateJob(CONTEXT_ID, job.getName(), job);
    }

    @Test
    public void testExportTestCaseResults() {
        DocumentList<TestCaseResultReport> response = controller.exportTestCaseResults(JOB_ID, EXECUTION_ID);

        assertThat(response)
            .as("response")
            .isNotNull();
        assertThat(response.getErrors())
            .as("response errors")
            .isNullOrEmpty();
        assertThat(response.getData())
            .as("response data")
            .isNotEmpty();
        assertThat(response.getMeta())
            .as("response meta")
            .isNotNull();
        assertThat(response.getMeta().getMeta())
            .as("response meta map")
            .isNotEmpty()
            .containsKey(CONTENT_DISPOSITION);

        List<TestCaseResultReport> testCaseResults = response.unwrap();

        assertThat(testCaseResults)
            .as("Test Case Result Report data")
            .hasSize(6)
            .doesNotContainNull()
            .doesNotHaveDuplicates();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getTestSuiteName)
            .as("test suite")
            .isNotEmpty();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getName)
            .as("test case name")
            .isNotEmpty();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getStartDate)
            .as("start date")
            .isNotNull();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getStopDate)
            .as("stop date")
            .isNotNull();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getDuration)
            .as("duration")
            .isNotNull();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getResultCode)
            .as("result code")
            .isNotEmpty();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getCreatedDate)
            .as("created date")
            .isNotNull();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getGroups)
            .as("groups")
            .isNotEmpty();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getComponents)
            .as("components")
            .isNotEmpty();

        assertThat(testCaseResults)
            .extracting(TestCaseResultReport::getIsoVersion)
            .as("iso version")
            .isNotEmpty();
    }

    @Test
    public void testExportTestCaseResultOfInvalidJobExecutionId() {
        assertThatThrownBy(() -> controller.exportTestCaseResults(INVALID_JOB_ID, INVALID_EXECUTION_ID))
            .isInstanceOf(NotFoundException.class);
    }

    private Date toDate(String date) {
        return dateConverter.fromString(date);
    }
}

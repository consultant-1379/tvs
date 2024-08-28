package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.converters.DateConverter;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Meta;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ExecutionTime;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSuiteResultRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.List;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.DESC;
import static org.assertj.core.api.Assertions.assertThat;

public class TestSuiteResultControllerTest extends AbstractIntegrationTest {

    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String EXECUTION_ID = "session_id_1";
    private static final String EMPTY_EXECUTION_ID = "session_id_0";
    private static final String SUITE_NAME = "testsuite_id_1";
    private static final String EMPTY_SUITE_NAME = "testsuite_id_0";
    private static final String TEST_SESSION_UUID = "TEST_SESSION_UUID";
    private static final String ID = "id";
    private static final String QUERY = null;

    private DateConverter dateConverter = new DateConverter(ZoneId.of("UTC"));

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Autowired
    private TestSuiteResultController controller;

    private TestSuiteResultBean testSuiteResultBean;

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);

        TestSession testSession = new TestSession();
        testSession.setExecutionId(TEST_SESSION_UUID);
        testSession.setUri("http://somelink.com");
        testSession.setTime(new ExecutionTime());

        testSessionRepository.save(testSession);

        ExecutionTimeBean timeBean = new ExecutionTimeBean();
        timeBean.setStartDate(dateConverter.fromString("2015-12-11T14:01:57.000"));
        timeBean.setStopDate(dateConverter.fromString("2015-12-11T14:36:57.500"));
        timeBean.setDuration(2100500L);

        testSuiteResultBean = new TestSuiteResultBean();
        testSuiteResultBean.setTime(timeBean);
        StatisticsBean statisticsBean = new StatisticsBean();
        statisticsBean.setTotal(2);
        statisticsBean.setPassed(1);
        statisticsBean.setCancelled(1);

        testSuiteResultBean.setCreatedDate(dateConverter.fromString("2015-12-11T14:01:29.000"));
        testSuiteResultBean.setStatistics(statisticsBean);
        testSuiteResultBean.setId(SUITE_NAME);
        testSuiteResultBean.setPassRate(50);
    }

    @Test
    public void getTestSuiteResult() {
        TestSuiteResultBean queried = controller.getTestSuiteResult(JOB_ID, EXECUTION_ID, SUITE_NAME)
            .unwrap();

        assertThat(queried)
            .isEqualToIgnoringGivenFields(testSuiteResultBean, "time", "statistics", "additionalFields");
        assertThat(queried.getTime())
            .isEqualToComparingFieldByField(testSuiteResultBean.getTime());
        assertThat(queried.getStatistics())
            .isEqualToComparingFieldByField(testSuiteResultBean.getStatistics());
    }

    @Test
    public void getTestSessionPage() {
        List<TestSuiteResultBean> queried = controller.getList(JOB_ID, EXECUTION_ID, 1, 20, ID, DESC, QUERY).unwrap();

        assertThat(queried)
            .hasSize(1)
            .doesNotContainNull();
    }

    @Test
    public void getChildrenInMeta() {
        Document<TestSuiteResultBean> response = controller.getTestSuiteResult(JOB_ID, EXECUTION_ID, SUITE_NAME);

        @SuppressWarnings("unchecked")
        List<String> children = (List<String>) response.getMeta().get(Meta.CHILDREN);
        assertThat(children)
            .containsOnly("TORF-507", "TORF-13126_FUNC_1", "TORF-12388_FUNC_1", "TORF-508", "TORF-508", "TORF-508");
    }

    @Test
    public void aggregateTestSuite() {
        TestSession testSession = testSessionRepository.findByExecutionId(EXECUTION_ID);
        TestSuiteResult suite = testSuiteResultRepository.findByTestSessionIdAndName(testSession.getId(), SUITE_NAME);

        suite.setStatistics(null);

        testSuiteResultRepository.save(suite);

        TestSuiteResultBean result = controller.aggregateTestSuiteResult(JOB_ID, EXECUTION_ID, SUITE_NAME).unwrap();

        assertThat(result)
            .isEqualToIgnoringGivenFields(
                testSuiteResultBean, "time", "statistics", "passRate", "modifiedDate", "additionalFields");

        assertThat(result.getTime())
            .isEqualToComparingFieldByField(testSuiteResultBean.getTime());

        StatisticsBean expectedStatistics = new StatisticsBean();
        expectedStatistics.setTotal(6);
        expectedStatistics.setPassed(3);
        expectedStatistics.setPending(1);
        expectedStatistics.setFailed(2);

        assertThat(result.getStatistics())
            .isEqualToComparingFieldByField(expectedStatistics);

        assertThat(result.getPassRate())
            .isEqualTo(50);
    }

    @Test
    public void aggregateEmptyTestSuite() {
        TestSuiteResultBean result = controller.aggregateTestSuiteResult(JOB_ID, EMPTY_EXECUTION_ID, EMPTY_SUITE_NAME)
            .unwrap();
        assertThat(result)
            .as("Test Suite result")
            .isNotNull();

        assertThat(result.getId())
            .as("Test Suite id")
            .isEqualTo(EMPTY_SUITE_NAME);

        assertThat(result.getTime())
            .as("Test Suite time")
            .isNotNull();
        assertThat(result.getStatistics())
            .as("Test Suite statistics")
            .isNotNull();

        assertThat(result.getTestCaseResults())
            .as("Test Suite test cases")
            .isNull();

        assertThat(result.getPassRate())
            .as("Test Suite pass rate")
            .isZero();
    }
}

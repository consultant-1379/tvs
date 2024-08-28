package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.converters.DateConverter;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ExecutionTime;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Statistics;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSuiteResultRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.List;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.*;
import static org.assertj.core.api.Assertions.*;

public class TestCaseControllerTest extends AbstractIntegrationTest {

    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String CONTEXT_ID = "systemId-1";

    private static final String EXECUTION_ID = "session_id_1";
    private static final String SUITE_NAME = "testsuite_id_1";
    private static final String CASE_NAME = "TORF-507";

    private static final String ID = "id";
    public static final String QUERY = null;

    private DateConverter dateConverter = new DateConverter(ZoneId.of("UTC"));

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Autowired
    private TestCaseResultController controller;

    private TestCaseResultBean testCaseResultBean;

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);

        TestSession testSession = new TestSession();
        testSession.setExecutionId(EXECUTION_ID);
        testSession.setUri("http://somelink.com");
        testSession.setTime(new ExecutionTime());

        testSessionRepository.save(testSession);

        TestSuiteResult testSuiteResult = new TestSuiteResult();
        testSuiteResult.setName(SUITE_NAME);
        testSuiteResult.setTime(new ExecutionTime());
        Statistics statistics = new Statistics();
        testSuiteResult.setStatistics(statistics);

        testSuiteResultRepository.save(testSuiteResult);

        ExecutionTimeBean timeBean = new ExecutionTimeBean();
        timeBean.setStartDate(dateConverter.fromString("2016-11-10T14:03:28.000"));
        timeBean.setStopDate(dateConverter.fromString("2016-11-10T14:03:28.630"));
        timeBean.setDuration(630L);

        testCaseResultBean = new TestCaseResultBean();
        testCaseResultBean.setId("TORF-507");
        testCaseResultBean.setResultCode(TestExecutionStatus.PASSED.name());
        testCaseResultBean.setTime(timeBean);
        testCaseResultBean.setCreatedDate(dateConverter.fromString("2015-12-11T14:01:28.000"));
    }

    @Test
    public void getTestCase() {
        TestCaseResultBean result = controller.getTestCaseResult(JOB_ID, EXECUTION_ID, SUITE_NAME, CASE_NAME).unwrap();

        assertThat(result)
            .isEqualToIgnoringGivenFields(testCaseResultBean, "time", "createdDate", "name", "importStatus",
                "additionalFields");
        // TODO compare time when timezone problem is solved
    }

    @Test
    public void getTestSessionPage() {
        List<TestCaseResultBean> result = controller.getList(JOB_ID, EXECUTION_ID, SUITE_NAME, 1, 20, ID, DESC, QUERY)
            .unwrap();

        assertThat(result)
            .hasSize(6)
            .doesNotContainNull();
    }

}

package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.application.services.TestSessionService;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.util.Date;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.DESC;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.uniqueString;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestSuiteResultInsertControllerTest extends AbstractIntegrationTest {

    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String SUITE_NAME = "testsuite_id_1";

    private static final String JOB_NAME = "X_SCHEDULER_1";
    private static final String CONTEXT_ID = "systemId-1";
    private static final String NON_EXISTING_CONTEXT_ID = "nonExistingSystemId";
    private static final String EXECUTION_ID = "session_id_1";
    private static final String NON_EXISTING_EXECUTION_ID = "nonExistingExecutionId";

    @Autowired
    private TestSuiteResultInsertController testSuiteResultInsertController;

    @Autowired
    private TestSuiteResultController controller;

    @Autowired
    private TestSessionService testSessionService;

    private TestSuiteResultBean testSuiteResultBean;

    @Before
    public void setUp() {
        testSuiteResultInsertController.setJsonApiUtil(mockedJsonApiUtil);
        controller.setJsonApiUtil(mockedJsonApiUtil);

        testSuiteResultBean = new TestSuiteResultBean();
        testSuiteResultBean.setTestCaseResults(emptyList());
        ExecutionTimeBean timeBean = new ExecutionTimeBean();
        timeBean.setStartDate(new Date());
        testSuiteResultBean.setTime(timeBean);
        testSuiteResultBean.setStatistics(new StatisticsBean());
    }

    @Test
    public void createNewTestSuite() {
        int suiteCount = controller.getList(JOB_ID, EXECUTION_ID, 1, 20, "id", DESC, null).unwrap().size();
        String newSuiteName = uniqueString();

        TestSuiteResultBean testSuite = testSuiteResultInsertController.updateTestSuiteResult(CONTEXT_ID, JOB_NAME,
            EXECUTION_ID, newSuiteName, testSuiteResultBean).unwrap();

        assertThat(testSuite).isNotNull();
        assertThat(testSuite.getTime())
            .isEqualToIgnoringGivenFields(testSuiteResultBean.getTime(), "duration");
        assertThat(testSuite.getStatistics())
            .isEqualToComparingFieldByField(testSuiteResultBean.getStatistics());
        assertThat(testSuite.getCreatedDate())
            .isNotNull();
        assertThat(
            testSessionService.getTestSession(JOB_ID, EXECUTION_ID).getTestSuiteCount()).isEqualTo(suiteCount + 1);
    }

    @Test
    public void testSuiteModified() {
        String testSuiteId = testSuiteResultInsertController.updateTestSuiteResult(CONTEXT_ID, JOB_NAME,
            EXECUTION_ID, SUITE_NAME, testSuiteResultBean).unwrap().getId();
        TestSuiteResultBean inserted = controller.getTestSuiteResult(JOB_ID, EXECUTION_ID, testSuiteId).unwrap();

        testSuiteId = testSuiteResultInsertController.updateTestSuiteResult(CONTEXT_ID, JOB_NAME, EXECUTION_ID,
            SUITE_NAME, testSuiteResultBean).unwrap().getId();
        TestSuiteResultBean updated = controller.getTestSuiteResult(JOB_ID, EXECUTION_ID, testSuiteId).unwrap();

        assertThat(updated.getId()).isEqualTo(inserted.getId());
        assertThat(updated.getModifiedDate()).isAfter(inserted.getModifiedDate());
    }

    @Test
    public void updateExistingTestSuite() {
        TestSuiteResultBean testSuite = controller.getTestSuiteResult(JOB_ID, EXECUTION_ID, SUITE_NAME).unwrap();

        StatisticsBean statistics = new StatisticsBean();
        statistics.setTotal(6);
        statistics.setCancelled(0);
        statistics.setBroken(0);
        statistics.setFailed(2);
        statistics.setPassed(3);
        statistics.setPending(1);

        testSuite.getTime().setStopDate(new Date());
        testSuite.getTime().setDuration(null);
        testSuite.setId(null);
        testSuite.setCreatedDate(null);
        testSuite.setTestCaseResults(emptyList());
        testSuite.setPassRate(null);

        TestSuiteResultBean updated = testSuiteResultInsertController
            .updateTestSuiteResult(CONTEXT_ID, JOB_NAME, EXECUTION_ID, SUITE_NAME, testSuite).unwrap();

        assertThat(updated.getTime())
            .isEqualToIgnoringGivenFields(testSuite.getTime(), "duration");
        assertThat(updated.getStatistics())
            .isEqualToComparingFieldByField(statistics);
        assertThat(updated.getCreatedDate())
            .isNotNull();
        assertThat(updated.getPassRate())
            .isEqualTo(50);
    }

    @Test
    public void createNewTestSuiteWithNonExistingJob() {
        assertThatThrownBy(() -> testSuiteResultInsertController.updateTestSuiteResult(NON_EXISTING_CONTEXT_ID,
            JOB_NAME, EXECUTION_ID, SUITE_NAME, testSuiteResultBean))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void createNewTestSuiteWithNonExistingJobSession() {
        assertThatThrownBy(() -> testSuiteResultInsertController.updateTestSuiteResult(CONTEXT_ID, JOB_NAME,
            NON_EXISTING_EXECUTION_ID, SUITE_NAME, testSuiteResultBean))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateInvalidTestSuiteBean() {
        testSuiteResultBean = null; // set null for constrain violation
        assertThatThrownBy(() -> testSuiteResultInsertController.updateTestSuiteResult(CONTEXT_ID, JOB_NAME,
            NON_EXISTING_EXECUTION_ID, SUITE_NAME, testSuiteResultBean))
            .isInstanceOf(ConstraintViolationException.class);
    }
}

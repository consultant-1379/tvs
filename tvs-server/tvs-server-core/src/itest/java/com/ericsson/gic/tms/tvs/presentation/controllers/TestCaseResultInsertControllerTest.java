package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.converters.DateConverter;
import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.application.services.TestSuiteResultService;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestCaseResultInsertControllerTest extends AbstractIntegrationTest {

    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String JOB_NAME = "X_SCHEDULER_1";
    private static final String NON_EXISITNG_JOB_NAME = "nonExistingJobName";
    private static final String CONTEXT_ID = "systemId-1";
    private static final String NON_EXISTING_CONTEXT_ID = "nonExistingSystemId";
    private static final String EXECUTION_ID = "session_id_1";
    private static final String SUITE_NAME = "testsuite_id_1";
    private static final String CASE_UID = "qweasd123456";
    private static final String CASE_NEW_NAME = "CASE_NEW_NAME";
    private static final String CASE_NEW_UID = "CASE_NEW_UID";

    private DateConverter dateConverter = new DateConverter(ZoneId.of("UTC"));

    @Autowired
    private TestCaseResultInsertController insertController;

    @Autowired
    private TestCaseResultController controller;

    @Autowired
    private TestSuiteResultService testSuiteResultService;

    private TestCaseResultBean testCaseResultBean;

    @Before
    public void setUp() {
        insertController.setJsonApiUtil(mockedJsonApiUtil);
        controller.setJsonApiUtil(mockedJsonApiUtil);

        ExecutionTimeBean timeBean = new ExecutionTimeBean();
        timeBean.setStartDate(dateConverter.fromString("2016-11-10T14:03:28.000"));
        timeBean.setStopDate(dateConverter.fromString("2016-11-10T14:03:28.630"));

        testCaseResultBean = new TestCaseResultBean();
        testCaseResultBean.setId(CASE_NEW_UID);
        testCaseResultBean.setName(CASE_NEW_NAME);
        testCaseResultBean.setResultCode(TestExecutionStatus.PASSED.name());
        testCaseResultBean.setTime(timeBean);
    }

    @Test
    public void testCreateNewTestCase() {
        int testCaseCount = controller.getList(JOB_ID, EXECUTION_ID, SUITE_NAME).unwrap().size();

        testCaseResultBean.setResultCode("SUCCESS");

        insertController.updateTestCaseResult(CONTEXT_ID, JOB_NAME, EXECUTION_ID, SUITE_NAME, CASE_NEW_UID,
            testCaseResultBean).unwrap();

        TestCaseResultBean testCase =
            controller.getTestCaseResult(JOB_ID, EXECUTION_ID, SUITE_NAME, CASE_NEW_UID).unwrap();

        assertThat(testCase).isNotNull();
        assertThat(testCase.getTime())
            .isEqualToIgnoringGivenFields(testCaseResultBean.getTime(), "duration");
        assertThat(testCase.getCreatedDate())
            .isNotNull();
        assertThat(testCase.getResultCode()).isEqualTo(TestExecutionStatus.PASSED.name());
        assertThat(testCase.getExternalResultCode()).isEqualTo("SUCCESS");

        assertThat(
            testSuiteResultService.getTestSuiteResult(JOB_ID, EXECUTION_ID, SUITE_NAME).getStatistics().getTotal())
            .isEqualTo(testCaseCount + 1);
    }

    @Test
    public void testCaseResultModified() {
        String testCaseResultId = insertController.updateTestCaseResult(CONTEXT_ID, JOB_NAME, EXECUTION_ID, SUITE_NAME,
            CASE_NEW_UID, testCaseResultBean).unwrap().getId();
        TestCaseResultBean inserted =
            controller.getTestCaseResult(JOB_ID, EXECUTION_ID, SUITE_NAME, testCaseResultId).unwrap();

        testCaseResultId = insertController.updateTestCaseResult(CONTEXT_ID, JOB_NAME, EXECUTION_ID, SUITE_NAME,
            CASE_NEW_UID, testCaseResultBean).unwrap().getId();
        TestCaseResultBean updated =
            controller.getTestCaseResult(JOB_ID, EXECUTION_ID, SUITE_NAME, testCaseResultId).unwrap();

        assertThat(updated.getId()).isEqualTo(inserted.getId());
        assertThat(updated.getModifiedDate()).isAfter(inserted.getModifiedDate());
    }

    @Test
    public void updateExistingTestCase() {
        TestCaseResultBean updated = insertController.updateTestCaseResult(CONTEXT_ID, JOB_NAME, EXECUTION_ID,
            SUITE_NAME, CASE_UID, testCaseResultBean).unwrap();

        assertThat(updated)
            .isEqualToComparingOnlyGivenFields(testCaseResultBean, "resultCode");
        assertThat(updated.getTime())
            .isEqualToIgnoringGivenFields(testCaseResultBean.getTime(), "duration");
        assertThat(updated.getCreatedDate())
            .isNotNull();
    }

    @Test
    public void updateInvalidTestCaseBean() {
        testCaseResultBean.getTime().setStartDate(null); // reset for constrain violation
        assertThatThrownBy(
            () -> insertController.updateTestCaseResult(NON_EXISTING_CONTEXT_ID, JOB_NAME, EXECUTION_ID,
                SUITE_NAME, CASE_UID, testCaseResultBean))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void updateTestCaseByNonExistingContext() {
        assertThatThrownBy(
            () -> insertController.updateTestCaseResult(NON_EXISTING_CONTEXT_ID, JOB_NAME, EXECUTION_ID,
                SUITE_NAME, CASE_UID, testCaseResultBean))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateTestCaseByNonExistingJobName() {
        assertThatThrownBy(
            () -> insertController.updateTestCaseResult(CONTEXT_ID, NON_EXISITNG_JOB_NAME, EXECUTION_ID,
                SUITE_NAME, CASE_UID, testCaseResultBean))
            .isInstanceOf(NotFoundException.class);
    }
}

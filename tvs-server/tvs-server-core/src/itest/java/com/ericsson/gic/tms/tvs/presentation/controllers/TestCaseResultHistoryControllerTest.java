package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.converters.DateConverter;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseImportStatus;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultHistoryBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.*;
import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestCaseImportStatus.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.PENDING;
import static com.google.common.collect.Lists.*;
import static org.assertj.core.api.Assertions.*;

public class TestCaseResultHistoryControllerTest extends AbstractIntegrationTest {

    private static final int PAGE = 1;
    private static final int SIZE = 20;
    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String CONTEXT_ID = "systemId-1";
    private static final String JOB_EXECUTION_ID = "session_id_1";
    private static final String SUITE_NAME = "testsuite_id_1";
    private static final String TEST_CASE_ID = "TORF-508";
    private static final String TEST_CASE_ID_2 = "TORF-517";
    private static final String CASE_NEW_NAME = "CASE_NEW_NAME";
    private static final String CASE_NEW_UID = "CASE_NEW_UID";

    private static final String STATUS = "resultCode";
    private static final String ID = "id";
    private static final String QUERY = null;

    @Autowired
    private TestCaseResultHistoryController controller;

    private TestCaseResultBean testCaseResultBean;

    private DateConverter dateConverter = new DateConverter(ZoneId.of("UTC"));

    @Before
    public void setUp() {

        ExecutionTimeBean timeBean = new ExecutionTimeBean();
        timeBean.setStartDate(dateConverter.fromString("2016-11-10T14:03:28.000"));
        timeBean.setStopDate(dateConverter.fromString("2016-11-10T14:03:28.630"));

        testCaseResultBean = new TestCaseResultBean();
        testCaseResultBean.setId(CASE_NEW_UID);
        testCaseResultBean.setName(CASE_NEW_NAME);
        testCaseResultBean.setResultCode(TestExecutionStatus.PASSED.name());
        testCaseResultBean.setTime(timeBean);
        testCaseResultBean.setImportStatus(IMPORTED);

        controller.setJsonApiUtil(mockedJsonApiUtil);
    }

    @Test
    public void getAllTestCaseResultsPage() {
        TestCaseImportStatus status = null;
        DocumentList<TestCaseResultBean> response = controller.getList(status, PAGE, SIZE);

        assertThat(response).isNotNull();
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getData()).isNotEmpty().hasSize(SIZE);
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getMeta())
            .isNotEmpty()
            .contains(
                entry(TOTAL_ITEMS, 21L),
                entry(ITEM_PER_PAGE, SIZE),
                entry(CURRENT_PAGE, PAGE));
    }

    @Test
    public void getTestCaseResultsPageByStatus() {
        TestCaseImportStatus status = IMPORTED;
        DocumentList<TestCaseResultBean> response = controller.getList(status, PAGE, SIZE);

        assertThat(response)
            .as("Response of test case results")
            .isNotNull();
        assertThat(response.getErrors())
            .as("Response of test case results")
            .isNullOrEmpty();
        assertThat(response.getData())
            .as("Response of test case results")
            .isNotEmpty().hasSize(8);
        assertThat(response.getMeta())
            .as("Response of test case results")
            .isNotNull();
        assertThat(response.getMeta().getMeta())
            .as("Response of test case results")
            .isNotEmpty()
            .contains(
                entry(TOTAL_ITEMS, 8L),
                entry(ITEM_PER_PAGE, SIZE),
                entry(CURRENT_PAGE, PAGE));

        List<TestCaseResultBean> testCaseResultBeans = response.unwrap();
        assertThat(testCaseResultBeans)
            .as("Test case results")
            .extracting(TestCaseResultBean::getImportStatus)
            .contains(status);
    }

    @Test
    public void getTestCaseResultHistoryPage() {
        DocumentList<TestCaseResultHistoryBean> response = controller.getList(TEST_CASE_ID, null, null,
                PAGE, SIZE, STATUS, DESC, QUERY);

        assertThat(response).isNotNull();
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getMeta())
            .contains(entry(TOTAL_ITEMS, 5L), entry(ITEM_PER_PAGE, SIZE), entry(CURRENT_PAGE, PAGE));

        List<TestCaseResultHistoryBean> result = response.unwrap();
        assertThat(result)
            .as("Response content")
            .hasSize(5)
            .doesNotContainNull();

        assertThat(result)
            .extracting(TestCaseResultHistoryBean::getContextId)
            .as("Context ID")
            .contains(CONTEXT_ID);
        assertThat(result)
            .extracting(TestCaseResultHistoryBean::getJobId)
            .as("Job ID")
            .contains(JOB_ID);
        assertThat(result)
            .extracting(TestCaseResultHistoryBean::getExecutionId)
            .as("Job Execution ID")
            .contains(JOB_EXECUTION_ID);
        assertThat(result)
            .extracting(TestCaseResultHistoryBean::getTestSuiteName)
            .as("Test Suite Name")
            .contains(SUITE_NAME);
        assertThat(result)
            .extracting(TestCaseResultHistoryBean::getCreatedDate)
            .as("Create Date")
            .isNotNull();
        assertThat(result)
            .extracting(TestCaseResultHistoryBean::getResultCode)
            .as("Result Code")
            .containsOnlyElementsOf(newArrayList(PASSED.name(), FAILED.name(), PENDING.name()));
    }

    @Test
    public void getTestCaseResultHistoryPageByDate() {

        // Date range in future - should return no TestCaseResults
        LocalDateTime startDate = LocalDateTime.parse("2017-11-10T14:03:28.000");
        LocalDateTime stopDate = LocalDateTime.parse("2017-11-10T14:03:28.630");

        DocumentList<TestCaseResultHistoryBean> response = controller.getList(TEST_CASE_ID, startDate, stopDate,
                PAGE, SIZE, STATUS, DESC, QUERY);
        List<TestCaseResultHistoryBean> result = response.unwrap();

        assertThat(result).hasSize(0);

        // Valid date range
        startDate = LocalDateTime.parse("2015-11-10T14:03:28.000");
        stopDate = LocalDateTime.parse("2017-11-10T14:03:28.630");

        response = controller.getList(TEST_CASE_ID, startDate, stopDate,
                PAGE, SIZE, STATUS, DESC, QUERY);
        result = response.unwrap();
        assertThat(result).hasSize(5);
    }

    @Test
    public void getEmptyTestCaseResultHistoryPage() {
        DocumentList<TestCaseResultHistoryBean> response =
                controller.getList("UNKNOWN", null, null, PAGE, SIZE, ID, DESC, null);

        assertThat(response).isNotNull();
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getMeta())
            .contains(entry(TOTAL_ITEMS, 0L), entry(ITEM_PER_PAGE, SIZE), entry(CURRENT_PAGE, 1));

        List<TestCaseResultHistoryBean> result = response.unwrap();
        assertThat(result).as("Response content").isEmpty();
    }

    @Test
    public void getTestCaseResultHistorySortedByStatusDesc() {
        DocumentList<TestCaseResultHistoryBean> response = controller.getList(TEST_CASE_ID, null, null,
                PAGE, SIZE, STATUS, DESC, null);
        List<TestCaseResultHistoryBean> result = response.unwrap();
        assertThat(result).as("Desc sort of result by Status Code")
            .isSortedAccordingTo((tcr1, tcr2) -> tcr2.getResultCode().compareTo(tcr1.getResultCode()));
    }

    @Test
    public void getTestCaseResultHistorySortedByStatusAsc() {
        DocumentList<TestCaseResultHistoryBean> response = controller.getList(TEST_CASE_ID, null, null,
                PAGE, SIZE, STATUS, ASC, null);
        List<TestCaseResultHistoryBean> result = response.unwrap();
        assertThat(result).as("Asc sort of result by Status Code")
            .isSortedAccordingTo((tcr1, tcr2) -> tcr1.getResultCode().compareTo(tcr2.getResultCode()));
    }

    @Test
    public void updateExistingTestCase() {
        TestCaseResultBean updated = controller.update(TEST_CASE_ID_2, testCaseResultBean).unwrap();

        assertThat(updated)
            .as("Updated Test Case Result")
            .isEqualToIgnoringGivenFields(testCaseResultBean, "time", "createdDate", "modifiedDate");

        assertThat(updated.getTime())
            .as("Test Case Result Time")
            .isEqualToIgnoringGivenFields(testCaseResultBean.getTime(), "duration");

        assertThat(updated.getCreatedDate())
            .as("Test Case Result Created By")
            .isNotNull();

        assertThat(updated.getModifiedDate())
            .as("Test Case Result Modified")
            .isNotNull();
    }
}

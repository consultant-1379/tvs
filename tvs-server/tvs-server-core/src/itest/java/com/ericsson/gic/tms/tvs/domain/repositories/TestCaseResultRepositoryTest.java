package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.ericsson.gic.tms.tvs.domain.model.verdict.ImportStatus.IMPORTED;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.*;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TestCaseResultRepositoryTest extends AbstractIntegrationTest {

    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String JOB_SESSION_ID_1 = "session_id_1";
    private static final String JOB_SESSION_ID_2 = "session_id_2";

    private static final String TEST_CASE_RESULT_ID_1 = "5644b7d9e4b03ebcdc2b0fc6";
    private static final String TEST_CASE_RESULT_ID_2 = "5644b7d9e4b03ebcdc2b0fc9";
    private static final String TEST_CASE_RESULT_ID_3 = "5644b7d9e4b03ebcdc2b0fca";

    private static final String TEST_CASE_NAME_1 = "testsuite_name_1";
    private static final String TEST_CASE_NAME_2 = "TORF-199924_FUNC_9";

    private static final String TEST_CASE_ID = "TORF-508";

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Test
    public void findByTestSuiteResultIdPageable() {
        Page<TestCaseResult> result = testCaseResultRepository
            .findByTestSuiteResultId(TEST_CASE_RESULT_ID_1, mock(Pageable.class), null);

        assertThat(result.getTotalElements()).isEqualTo(6);

        TestCaseResult resultTestCaseResult = result.getContent().get(0);
        assertThat(resultTestCaseResult.getTestSuiteResultId()).isEqualTo(TEST_CASE_RESULT_ID_1);
    }

    @Test
    public void findByNamePageable() {
        Page<TestCaseResult> result =
            testCaseResultRepository.findByName(TEST_CASE_NAME_2, null, null, mock(Pageable.class), null);

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent())
            .extracting(TestCaseResult::getId)
            .containsOnly(
                "5644b7d9e4b03ebcdc2b0fca",
                "5644b7d9e4b03ebcdc2b0fcb",
                "5644b7d9e4b03ebcdc2b0fcc");
    }

    @Test
    public void findByTestCaseIdPageable() {
        Page<TestCaseResult> result = testCaseResultRepository
            .findByTestCaseId(TEST_CASE_ID, mock(Pageable.class));

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent())
            .hasSize(5)
            .doesNotContainNull();

        assertThat(result.getContent())
            .extracting(TestCaseResult::getContextId)
            .as("Context ID")
            .contains("systemId-1");
        assertThat(result.getContent())
            .extracting(TestCaseResult::getJobId)
            .as("Job ID")
            .contains(JOB_ID);
        assertThat(result.getContent())
            .extracting(TestCaseResult::getTestCaseId)
            .as("Test Case ID")
            .contains(TEST_CASE_ID);
        assertThat(result.getContent())
            .extracting(TestCaseResult::getCreatedDate)
            .as("Create Date")
            .isNotNull();
        assertThat(result.getContent())
            .extracting(TestCaseResult::getTestSuiteResultId)
            .as("Test Suite Result ID")
            .contains(TEST_CASE_RESULT_ID_1, TEST_CASE_RESULT_ID_2);
        assertThat(result.getContent())
            .extracting(TestCaseResult::getTime)
            .as("Time")
            .isNotNull();
        assertThat(result.getContent())
            .extracting(TestCaseResult::getResultCode)
            .as("Result Code")
            .containsOnlyElementsOf(newArrayList(
                PASSED.name(), FAILED.name(), PENDING.name(), PENDING.name(), PASSED.name()));
        assertThat(result.getContent())
            .extracting(TestCaseResult::getExecutionId)
            .as("Job Session ID")
            .contains(JOB_SESSION_ID_1, JOB_SESSION_ID_2);
    }

    @Test
    public void findByTestSuiteResultId() {
        List<TestCaseResult> result = testCaseResultRepository.findByTestSuiteResultId(TEST_CASE_RESULT_ID_2);
        assertThat(result.size()).isEqualTo(6);

        TestCaseResult resultTestCaseResult = result.get(0);
        assertThat(resultTestCaseResult.getTestSuiteResultId()).isEqualTo(TEST_CASE_RESULT_ID_2);
    }

    @Test
    public void findByNameAndTestSuiteResultId() {
        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setTestSuiteResultId(TEST_CASE_RESULT_ID_3);
        testCaseResult.setTestCaseId(TEST_CASE_NAME_1);
        testCaseResult.setImportStatus(IMPORTED);
        testCaseResult.addAdditionalFields(GROUPS, newArrayList("Performance Test", "Robustness"));
        testCaseResult.addAdditionalFields(REQUIREMENTS, newArrayList("TORF-72938", "TORF-59817"));
        testCaseResult.addAdditionalFields(PRIORITY, "Minor");
        testCaseResult.addAdditionalFields(COMPONENTS, newArrayList("TAF TE"));

        testCaseResultRepository.save(testCaseResult);

        TestCaseResult result = testCaseResultRepository.findByTestCaseIdAndAndTestSuiteResultId(
            TEST_CASE_NAME_1,
            TEST_CASE_RESULT_ID_3);

        assertThat(result)
            .as("Test Case Result")
            .isEqualToIgnoringGivenFields(testCaseResult, "modifiedDate", "createdDate");

        assertThat(result.getModifiedDate())
            .as("Modified Date")
            .isNotNull();
        assertThat(result.getCreatedDate())
            .as("Create Date")
            .isNotNull();
    }
}



package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.application.services.JobService;
import com.ericsson.gic.tms.tvs.application.util.VersionConverter;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseBean;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseReport;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseResultBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.COMPONENTS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class TestCaseFlakinessControllerTest extends AbstractIntegrationTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private TestCaseFlakinessController controller;

    private Date startOfTest;
    private static final String DROP_1 = "16.1";
    private static final String ISO_VERSION_1 = "1.18.2";

    @Before
    public void setUp() {
        startOfTest = new Date();
        mongoFixtures.dropCollection(JOB.getName());
        mongoFixtures.dropCollection(TEST_SESSION.getName());
        mongoFixtures.dropCollection(TEST_SUITE_RESULT.getName());
        mongoFixtures.dropCollection(TEST_CASE_RESULT.getName());
        controller.setJsonApiUtil(mockedJsonApiUtil);
    }

    @Test
    public void testGetFlakyTests() throws Exception {
        String testCaseId1 = uniqueString();
        String testCaseId2 = uniqueString();

        List<String> testCaseIds = new ArrayList<>();
        testCaseIds.add(testCaseId1);
        testCaseIds.add(testCaseId2);

        String executionId1 = uniqueString();
        String executionId2 = uniqueString();
        String executionId3 = uniqueString();

        List<String> executionIds = new ArrayList<>();
        executionIds.add(executionId1);
        executionIds.add(executionId2);
        executionIds.add(executionId3);

        JobBean job = prepareData(testCaseIds, executionIds);

        DocumentList<?> flakiness =
                controller.getAllTestCaseFlakinessByJob(job.getId(), ldtMoment(3), ldtMoment(14), 100);

        assertThat(flakiness.unwrap()).hasSize(2);

        FlakyTestCaseBean testCase1 = (FlakyTestCaseBean) flakiness.unwrap().get(1);
        FlakyTestCaseBean testCase2 = (FlakyTestCaseBean) flakiness.unwrap().get(0);
        Map<String, FlakyTestCaseResultBean> testCase1Results = testCase1.getLatestResults();
        Map<String, FlakyTestCaseResultBean> testCase2Results = testCase2.getLatestResults();

        assertThat(testCase1.getId()).isEqualTo(testCaseId1);
        assertThat(testCase1.getFlakiness()).isEqualTo(50);
        assertThat(testCase1Results).hasSize(2);
        assertThat(testCase1Results).containsOnlyKeys(executionId2, executionId3);
        assertThat(testCase2.getId()).isEqualTo(testCaseId2);
        assertThat(testCase2.getFlakiness()).isEqualTo(67);
        assertThat(testCase2Results).hasSize(2);
        assertThat(testCase2Results).containsOnlyKeys(executionId1, executionId2);

        assertThat(testCase1Results.get(executionId2).getResultCode()).isEqualTo(FAILED);
        assertThat(testCase1Results.get(executionId3).getResultCode()).isEqualTo(PASSED);
        assertThat(testCase2Results.get(executionId1).getResultCode()).isEqualTo(FAILED);
        assertThat(testCase2Results.get(executionId2).getResultCode()).isEqualTo(FAILED);

        assertThat(testCase2.getComponents()).containsOnly("component_1", "component_3");
        assertThat(testCase2.getSuites()).containsOnly("suite_2", "suite_1", "suite_3");
    }

    @Test
    public void testGetFlakyTestsForRangeOfDates() throws Exception {
        String testCaseId1 = uniqueString();
        String testCaseId2 = uniqueString();

        List<String> testCaseIds = new ArrayList<>();
        testCaseIds.add(testCaseId1);
        testCaseIds.add(testCaseId2);

        String executionId1 = uniqueString();
        String executionId2 = uniqueString();
        String executionId3 = uniqueString();

        List<String> executionIds = new ArrayList<>();
        executionIds.add(executionId1);
        executionIds.add(executionId2);
        executionIds.add(executionId3);

        JobBean job = prepareData(testCaseIds, executionIds);

        DocumentList<?> flakiness =
                controller.getAllTestCaseFlakinessByJob(job.getId(), ldtMoment(3), ldtMoment(14), 100);

        assertThat(flakiness.unwrap()).hasSize(2);

        FlakyTestCaseBean testCase1 = (FlakyTestCaseBean) flakiness.unwrap().get(1);
        FlakyTestCaseBean testCase2 = (FlakyTestCaseBean) flakiness.unwrap().get(0);
        Map<String, FlakyTestCaseResultBean> testCase1Results = testCase1.getLatestResults();
        Map<String, FlakyTestCaseResultBean> testCase2Results = testCase2.getLatestResults();

        assertThat(testCase1.getId()).isEqualTo(testCaseId1);
        assertThat(testCase1.getFlakiness()).isEqualTo(50);
        assertThat(testCase1Results).hasSize(2);
        assertThat(testCase1Results).containsOnlyKeys(executionId2, executionId3);
        assertThat(testCase2.getId()).isEqualTo(testCaseId2);
        assertThat(testCase2.getFlakiness()).isEqualTo(67);
        assertThat(testCase2Results).hasSize(2);
        assertThat(testCase2Results).containsOnlyKeys(executionId1, executionId2);

        assertThat(testCase1Results.get(executionId2).getResultCode()).isEqualTo(FAILED);
        assertThat(testCase1Results.get(executionId3).getResultCode()).isEqualTo(PASSED);
        assertThat(testCase2Results.get(executionId1).getResultCode()).isEqualTo(FAILED);
        assertThat(testCase2Results.get(executionId2).getResultCode()).isEqualTo(FAILED);

        // Date range changed to smaller interval
        flakiness =
                controller.getAllTestCaseFlakinessByJob(job.getId(), ldtMoment(7), ldtMoment(12), 100);

        assertThat(flakiness.unwrap()).hasSize(2);

        testCase1 = (FlakyTestCaseBean) flakiness.unwrap().get(1); // TC1 is less flaky
        testCase2 = (FlakyTestCaseBean) flakiness.unwrap().get(0); // TC2 is more flaky, so comes first
        testCase1Results = testCase1.getLatestResults();
        testCase2Results = testCase2.getLatestResults();

        assertThat(testCase1.getId()).isEqualTo(testCaseId1);
        assertThat(testCase1.getFlakiness()).isEqualTo(0);
        assertThat(testCase1Results).hasSize(1);
        assertThat(testCase1Results).containsOnlyKeys(executionId3);
        assertThat(testCase2.getId()).isEqualTo(testCaseId2);
        assertThat(testCase2.getFlakiness()).isEqualTo(50);
        assertThat(testCase2Results).hasSize(1);
        assertThat(testCase2Results).containsOnlyKeys(executionId2);

        assertThat(testCase1Results.get(executionId3).getResultCode()).isEqualTo(PASSED);
        assertThat(testCase2Results.get(executionId2).getResultCode()).isEqualTo(FAILED);
    }

    @Test
    public void testGetSingleFlakyTest() throws Exception {
        String testCaseId1 = uniqueString();
        String testCaseId2 = uniqueString();

        List<String> testCaseIds = new ArrayList<>();
        testCaseIds.add(testCaseId1);
        testCaseIds.add(testCaseId2);

        String executionId1 = uniqueString();
        String executionId2 = uniqueString();
        String executionId3 = uniqueString();

        List<String> executionIds = new ArrayList<>();
        executionIds.add(executionId1);
        executionIds.add(executionId2);
        executionIds.add(executionId3);

        JobBean job = prepareData(testCaseIds, executionIds);

        Document<?> flakiness =
                controller.getSingleTestCaseFlakiness(job.getId(), testCaseId1, ldtMoment(3), ldtMoment(12));

        FlakyTestCaseBean testCase1 = (FlakyTestCaseBean) flakiness.unwrap();
        Map<String, FlakyTestCaseResultBean> testCase1Results = testCase1.getLatestResults();

        assertThat(testCase1.getId()).isEqualTo(testCaseId1);
        assertThat(testCase1.getFlakiness()).isEqualTo(50);
        assertThat(testCase1Results).hasSize(2);
        assertThat(testCase1Results).containsOnlyKeys(executionId3, executionId2);

        assertThat(testCase1Results.get(executionId3).getResultCode()).isEqualTo(PASSED);
        assertThat(testCase1Results.get(executionId2).getResultCode()).isEqualTo(FAILED);
    }

    @Test
    public void exportTestCaseFlakinessReport() {

        String testCaseId1 = uniqueString();
        String testCaseId2 = uniqueString();

        List<String> testCaseIds = new ArrayList<>();
        testCaseIds.add(testCaseId1);
        testCaseIds.add(testCaseId2);

        String executionId1 = uniqueString();
        String executionId2 = uniqueString();
        String executionId3 = uniqueString();

        List<String> executionIds = new ArrayList<>();
        executionIds.add(executionId1);
        executionIds.add(executionId2);
        executionIds.add(executionId3);

        JobBean job = prepareData(testCaseIds, executionIds);

        List<FlakyTestCaseReport> flakiness =
            controller.exportTestCaseFlakinessReportByJob(job.getId(), ldtMoment(3), ldtMoment(14), 100).unwrap();

        assertThat(flakiness).hasSize(2);

        FlakyTestCaseReport testCase1 = flakiness.get(1);
        FlakyTestCaseReport testCase2 = flakiness.get(0);

        assertThat(testCase1.getId()).isEqualTo(testCaseId1);
        assertThat(testCase1.getFlakiness()).isEqualTo(50);
        assertThat(testCase2.getId()).isEqualTo(testCaseId2);
        assertThat(testCase2.getFlakiness()).isEqualTo(67);
    }

    /**
     * Returns a date
     * from 1 hour before start of test,
     * after delta seconds
     */
    private Date dateMoment(int delta) {
        return DateUtils.toDate(ldtMoment(delta));
    }

    private LocalDateTime ldtMoment(int delta) {
        return DateUtils.toLocalDateTime(startOfTest).minusHours(1).plusSeconds(delta);
    }

    private TestCaseResultBean testCaseResultWithAdditionalData(String nameAndId, String resultCode,
                                                                String component, int startDate, int endDate) {
        TestCaseResultBean testCaseResultBean =
                testCaseResult(nameAndId, resultCode, dateMoment(startDate), dateMoment(endDate));
        testCaseResultBean.addAdditionalFields(COMPONENTS, newArrayList(component));
        return testCaseResultBean;
    }

    private JobBean prepareData(List<String> testCaseIds, List<String> executionIds) {

        JobBean job = job(
                testSession(executionIds.get(0),
                        testSuiteResult("suite_1",
                                testCaseResultWithAdditionalData(testCaseIds.get(0), PASSED, "component_1", 1, 2),
                                testCaseResultWithAdditionalData(testCaseIds.get(1), FAILED, "component_1", 3, 4)
                        )
                ),
                testSession(executionIds.get(1),
                        testSuiteResult("suite_2",
                                testCaseResultWithAdditionalData(testCaseIds.get(0), FAILED, "component_3", 5, 6),
                                testCaseResultWithAdditionalData(testCaseIds.get(1), PASSED, "component_1", 7, 8)
                        ),
                        testSuiteResult("suite_3",
                                testCaseResultWithAdditionalData(testCaseIds.get(1), FAILED, "component_3", 9, 10)
                        )
                ),
                testSession(executionIds.get(2),
                        testSuiteResult("suite_4",
                                testCaseResultWithAdditionalData(testCaseIds.get(0), PASSED, "component_4", 11, 12)
                        )
                ),
                testSessionWithIgnored(ISO_VERSION_1,
                    VersionConverter.getPaddedVersion(ISO_VERSION_1),
                    DROP_1,
                        testSuiteResult("suite_5",
                                testCaseResultWithAdditionalData(testCaseIds.get(1), PASSED, "component_5", 13, 14)
                        )
                )
        );
        String contextId = uniqueString();
        JobBean createdJob = jobService.updateJob(contextId, job.getName(), job);
        return createdJob;
    }
}

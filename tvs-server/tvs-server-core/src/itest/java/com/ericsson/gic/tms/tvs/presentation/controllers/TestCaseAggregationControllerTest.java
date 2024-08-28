package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ExecutionTime;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSuiteResultRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseIdBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestVerdictBean;
import com.github.mongobee.exception.MongobeeException;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

public class TestCaseAggregationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Autowired
    private TestCaseAggregationController testCaseAggregationController;

    private String testCaseName;
    private String anotherTestCaseName;
    private String contextId;

    @Before
    public void setUp() throws MongobeeException {
        mongoFixtures.dropDatabase();
        mongobee.execute();
        testCaseAggregationController.setJsonApiUtil(mockedJsonApiUtil);

        testCaseName = randomString();
        anotherTestCaseName = randomString();
        contextId = randomString();

        TestSuiteResult testSuite1 = getTestSuiteResult();
        TestSuiteResult testSuite2 = getTestSuiteResult();

        TestCaseResult tc1 = new TestCaseResult();
        tc1.setTestSuiteResultId(testSuite1.getId());
        tc1.setTestCaseId(randomString());
        tc1.setName(testCaseName);
        tc1.setResultCode(PASSED.name());
        tc1.setCreatedDate(new Date(330));
        tc1.setTime(new ExecutionTime(new Date(200), new Date(400)));

        TestCaseResult tc2 = new TestCaseResult();
        tc2.setTestSuiteResultId(testSuite1.getId());
        tc2.setTestCaseId(randomString());
        tc2.setName(testCaseName);
        tc2.setResultCode(FAILED.name());
        tc2.setCreatedDate(new Date(340));
        tc2.setTime(new ExecutionTime(new Date(200), new Date(600)));

        TestCaseResult tc3 = new TestCaseResult();
        tc3.setTestSuiteResultId(testSuite2.getId());
        tc3.setTestCaseId(randomString());
        tc3.setName(testCaseName);
        tc3.setResultCode(BROKEN.name());
        tc3.setCreatedDate(new Date(350));
        tc3.setTime(new ExecutionTime(new Date(200), new Date(500)));

        TestCaseResult tc4 = new TestCaseResult();
        tc4.setTestSuiteResultId(testSuite2.getId());
        tc4.setTestCaseId(randomString());
        tc4.setName(anotherTestCaseName);
        tc4.setResultCode(BROKEN.name());
        tc4.setCreatedDate(new Date(360));
        tc4.setTime(new ExecutionTime(new Date(200), new Date(500)));

        testCaseResultRepository.save(Lists.newArrayList(tc1, tc2, tc3, tc4));
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private TestSuiteResult getTestSuiteResult() {
        Job job = new Job();
        String uid = randomString();
        job.setUid(uid);
        job.setName(uid);
        job.setContextId(contextId);
        job = jobRepository.save(job);

        TestSession testSession = new TestSession();
        testSession.setJobId(job.getUid());
        testSession = testSessionRepository.save(testSession);

        TestSuiteResult testSuiteResult = new TestSuiteResult();
        testSuiteResult.setTestSessionId(testSession.getId());
        return testSuiteResultRepository.save(testSuiteResult);
    }

    @Test
    public void testAggregate() {
        TestVerdictBean expected = new TestVerdictBean();
        expected.setContextId(contextId);
        expected.setSystemId(testCaseName);
        expected.setAverageDuration(300L);
        expected.setErrorCount(1);
        expected.setFailureCount(1);
        expected.setSuccessCount(1);
        expected.setExecutionCount(3);

        TestVerdictBean testResultBean = testCaseAggregationController.getTestResult(testCaseName).unwrap();

        assertThat(testResultBean)
            .isNotNull()
            .isEqualToIgnoringGivenFields(expected, "lastExecutionDate");

        assertThat(testResultBean.getLastExecutionDate())
            .isNotNull();
    }

    @Test
    public void testTestResultIds() {
        List<TestCaseIdBean> result = testCaseAggregationController.getTestResultIds(null).unwrap();
        assertThat(result)
            .isNotNull()
            .doesNotContainNull();

        List<String> actualTestCaseIds = result.stream()
            .map(TestCaseIdBean::getSystemId)
            .collect(toList());

        assertThat(actualTestCaseIds)
            .containsOnly(testCaseName, anotherTestCaseName);
    }

    @Test
    public void testResultIdsFromDate() {
        List<TestCaseIdBean> result =
            testCaseAggregationController.getTestResultIds(LocalDateTime.parse("1970-01-01T00:00:00.345")).unwrap();
        assertThat(result)
            .isNotNull()
            .doesNotContainNull()
            .hasSize(2);
    }

}

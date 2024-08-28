package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.HasTime;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.DROP_NAME;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.ISO_VERSION;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.ISO_VERSION_PADDED;

public final class TvsBeanFactory {

    public static final String PASSED = "PASSED";
    public static final String PENDING = "PENDING";
    public static final String CANCELLED = "CANCELLED";
    public static final String FAILED = "FAILED";
    public static final String BROKEN = "BROKEN";

    private TvsBeanFactory() {
    }

    public static JobBean job(TestSessionBean... testSessions) {
        JobBean job = new JobBean();
        job.setTestSessions(Arrays.asList(testSessions));
        return job;
    }

    public static TestSessionBean testSession(TestSuiteResultBean... testSuiteResults) {
        return testSession(uniqueString(), testSuiteResults);
    }

    public static TestSessionBean testSession(String id, TestSuiteResultBean... testSuiteResults) {
        TestSessionBean testSession = new TestSessionBean();
        testSession.setId(id);
        testSession.setTime(executionTime(testSuiteResults));
        testSession.setUri("http://example.com/" + uniqueString());
        testSession.setTestSuites(Arrays.asList(testSuiteResults));
        return testSession;
    }

    public static TestSessionBean testSessionWithAdditionalData(String iso,
                                                                String isoVersionPadded,
                                                                String dropName,
                                                                TestSuiteResultBean... testSuiteResults) {
        TestSessionBean testSessionBean = testSession(uniqueString(), testSuiteResults);
        testSessionBean.addAdditionalFields(ISO_VERSION, iso);
        testSessionBean.addAdditionalFields(ISO_VERSION_PADDED, isoVersionPadded);
        testSessionBean.addAdditionalFields(DROP_NAME, dropName);

        return testSessionBean;
    }

    public static TestSessionBean testSessionWithIgnored(String iso, String isoVersionPadded,
                                                         String dropName, TestSuiteResultBean... testSuiteResults) {
        TestSessionBean testSessionBean =
            testSessionWithAdditionalData(iso, isoVersionPadded, dropName, testSuiteResults);
        testSessionBean.setIgnored(true);
        return testSessionBean;
    }

    public static TestSuiteResultBean testSuiteResult(TestCaseResultBean... testCaseResults) {
        return testSuiteResult(uniqueString(), testCaseResults);
    }

    public static TestSuiteResultBean testSuiteResult(String id, TestCaseResultBean... testCaseResults) {
        TestSuiteResultBean testSuiteResult = new TestSuiteResultBean();
        testSuiteResult.setId(id);
        testSuiteResult.setTime(executionTime(testCaseResults));
        testSuiteResult.setStatistics(statistics(testCaseResults));
        testSuiteResult.setTestCaseResults(Arrays.asList(testCaseResults));
        return testSuiteResult;
    }

    public static TestCaseResultBean testCaseResult() {
        return testCaseResult(uniqueString(), PASSED);
    }

    public static TestCaseResultBean testCaseResult(String testCaseId) {
        return testCaseResult(testCaseId, PASSED);
    }

    public static TestCaseResultBean testCaseResult(String testCaseId,
                                                    String resultCode) {
        Date stop = new Date();
        Date start = DateUtils.mapDate(stop, t -> t.minusSeconds(1));
        return testCaseResult(testCaseId, resultCode, start, stop);
    }

    public static TestCaseResultBean testCaseResult(String testCaseId,
                                                    String resultCode,
                                                    Date start,
                                                    Date stop) {
        TestCaseResultBean testCaseResult = new TestCaseResultBean();
        testCaseResult.setId(testCaseId);
        testCaseResult.setName(testCaseId);
        testCaseResult.setTime(executionTime(start, stop));
        testCaseResult.setResultCode(resultCode);
        return testCaseResult;
    }

    public static ExecutionTimeBean executionTime(HasTime[] timedBeans) {
        Date end = Arrays.stream(timedBeans)
            .reduce((a, b) -> b)
            .map(HasTime::getTime)
            .map(ExecutionTimeBean::getStartDate)
            .orElse(new Date());
        Date start = Arrays.stream(timedBeans)
            .findFirst()
            .map(HasTime::getTime)
            .map(ExecutionTimeBean::getStartDate)
            .orElse(DateUtils.mapDate(end, t -> t.minusSeconds(1)));
        return executionTime(start, end);
    }

    public static ExecutionTimeBean executionTime(Date start, Date stop) {
        ExecutionTimeBean time = new ExecutionTimeBean();
        time.setStartDate(start);
        time.setStopDate(stop);
        return time;
    }

    public static Date fromNow(Function<LocalDateTime, LocalDateTime> mapper) {
        return DateUtils.toDate(mapper.apply(LocalDateTime.now()));
    }

    public static StatisticsBean statistics(TestCaseResultBean... testCaseResults) {
        StatisticsBean statistics = new StatisticsBean();
        statistics.setTotal(testCaseResults.length);
        statistics.setPassed(countResultCodes(testCaseResults, PASSED));
        statistics.setPending(countResultCodes(testCaseResults, PENDING));
        statistics.setCancelled(countResultCodes(testCaseResults, CANCELLED));
        statistics.setFailed(countResultCodes(testCaseResults, FAILED));
        statistics.setBroken(countResultCodes(testCaseResults, BROKEN));
        return statistics;
    }

    private static int countResultCodes(TestCaseResultBean[] testCaseResults, String resultCode) {
        return (int) Arrays.stream(testCaseResults)
            .map(TestCaseResultBean::getResultCode)
            .filter(resultCode::equals)
            .count();
    }

    public static String uniqueString() {
        return UUID.randomUUID().toString();
    }
}

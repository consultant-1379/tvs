package com.ericsson.gic.tms.tvs.application.service;

import com.ericsson.gic.tms.RestClientFacade;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Meta;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import com.ericsson.gic.tms.tvs.presentation.resources.JobInsertResource;
import com.ericsson.gic.tms.tvs.presentation.resources.JobResource;
import com.ericsson.gic.tms.tvs.presentation.resources.TestCaseResultResource;
import com.ericsson.gic.tms.tvs.presentation.resources.TestSessionResource;
import com.ericsson.gic.tms.tvs.presentation.resources.TestSuiteResultResource;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.WebTarget;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.DROP_NAME;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.ISO_VERSION;
import static com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode.SELF;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toMap;

public class TestCaseResultBatchImportService {

    private static final Logger LOGGER = Logger.getLogger(TestCaseResultBatchImportService.class.getName());
    private static final String CONTEXT = "systemId-1";
    public static final String NOT_STARTED_CODE = "NOT_STARTED";

    private JobInsertResource jobInsertResource;

    private JobResource jobResource;

    private TestCaseResultResource testCaseResultResource;

    private final TestSuiteResultResource testSuiteResultResource;

    private final TestSessionResource testSessionResource;

    public TestCaseResultBatchImportService(String tvsUrl) throws NoSuchAlgorithmException {
        ResourceProvider resourceProvider = new ResourceProvider();
        WebTarget webTarget = resourceProvider.provideRestClient(tvsUrl);
        RestClientFacade restClientFacade = resourceProvider.getRestClientFacade(webTarget);
        this.jobInsertResource = resourceProvider.getJobInsertResource(restClientFacade);
        this.jobResource = resourceProvider.getJobResource(restClientFacade);
        this.testCaseResultResource = resourceProvider.getTestCaseResultResource(restClientFacade);
        this.testSuiteResultResource = resourceProvider.getTestSuiteResultResource(restClientFacade);
        this.testSessionResource = resourceProvider.getTestSessionResource(restClientFacade);
    }

    public void importTestCaseResults(String testActivity, String dropName, String isoVersion,
                                      List<TestCaseResultBean> testCaseResults) {
        List<TestCaseResultBean> finalResults = newArrayList();
        try {
            JobBean job = findJob(CONTEXT, testActivity);
            if (job != null) {
                finalResults = loadExistingResults(job.getId(), isoVersion, isoVersion);
            }
        } catch (NotFoundException ex) {
            LOGGER.info("Existing results not found. Creating new session");
        }

        if (!finalResults.isEmpty()) {
            finalResults = mergeTestCaseResults(finalResults, testCaseResults);
        } else {
            finalResults = testCaseResults;
        }

        ExecutionTimeBean overallTime = calculateOverallExecutionTime(testCaseResults);

        TestSuiteResultBean testSuiteResult = new TestSuiteResultBean();
        testSuiteResult.setId(isoVersion);
        testSuiteResult.setTime(overallTime);
        testSuiteResult.setStatistics(calculateStatistics(finalResults));
        testSuiteResult.setTestCaseResults(finalResults);

        testCaseResults.forEach((result) -> {
            result.addAdditionalFields(DROP_NAME, dropName);
            result.addAdditionalFields(ISO_VERSION, isoVersion);
        });

        TestSessionBean testSession = new TestSessionBean();
        testSession.setExecutionId(isoVersion);
        testSession.setTime(overallTime);
        testSession.setTestSuites(newArrayList(testSuiteResult));
        testSession.addAdditionalFields(DROP_NAME, dropName);
        testSession.addAdditionalFields(ISO_VERSION, isoVersion);

        JobBean job = new JobBean();
        job.setTestSessions(newArrayList(testSession));

        JobBean newJob = jobInsertResource.updateJob(CONTEXT, testActivity, job).unwrap();

        aggregate(newJob.getId(), isoVersion, isoVersion);
    }

    private JobBean findJob(String context, String jobName) {
        List<JobBean> jobs = jobResource.getList(context, 1, 1, null, null, "name=string|" + jobName).unwrap();
        return Iterables.getFirst(jobs, null);
    }

    private List<TestCaseResultBean> loadExistingResults(String jobId, String executionId, String suiteName) {
        DocumentList<TestCaseResultBean> results = testCaseResultResource
            .getList(jobId, executionId, suiteName, 1, 300, null, null, null);

        int pageCount = ((int) results.getMeta().get(Meta.TOTAL_ITEMS)) / 300;

        List<TestCaseResultBean> totalResult = results.unwrap();

        if (pageCount > 1) {
            IntStream.range(2, pageCount)
                .forEach(page ->
                    totalResult.addAll(testCaseResultResource
                        .getList(jobId, executionId, suiteName, page, 300, null, null, null).unwrap())
                );
        }

        return totalResult;
    }

    @VisibleForTesting
    List<TestCaseResultBean> mergeTestCaseResults(List<TestCaseResultBean> results,
                                                          List<TestCaseResultBean> results2) {

        Map<String, TestCaseResultBean> resultIndex = Stream.concat(results.stream(), results2.stream())
            .peek(TestCaseResultBean::resetImmutableFields)
            .collect(toMap(TestCaseResultBean::getId, bean -> bean, (beanA, beanB) -> {
                String resultCodeA = beanA.getResultCode();
                String resultCodeB = beanB.getResultCode();

                if (!resultCodeA.equals(resultCodeB) && resultCodeB.equals(NOT_STARTED_CODE)) {
                    return beanA;
                }

                return beanB;
            }));

        return newArrayList(resultIndex.values());
    }

    private StatisticsBean calculateStatistics(List<TestCaseResultBean> testCaseResults) {
        StatisticsBean statistics = new StatisticsBean();

        testCaseResults.forEach(testCaseResult ->
            TestExecutionStatus.getByStatus(testCaseResult.getResultCode())
                .ifPresent(status -> status.appendStatistics(statistics))
        );

        statistics.setTotal(testCaseResults.size());

        return statistics;
    }

    private ExecutionTimeBean calculateOverallExecutionTime(List<TestCaseResultBean> results) {
        Date startDate = new Date();
        Date stopDate = null;

        for (TestCaseResultBean result : results) {
            ExecutionTimeBean time = result.getTime();
            if (time != null) {
                startDate = getMinDate(startDate, time.getStartDate());
                stopDate = getMaxDate(stopDate, time.getStopDate());
            }
        }

        ExecutionTimeBean bean = new ExecutionTimeBean();
        bean.setStartDate(startDate);
        bean.setStopDate(stopDate);
        return bean;
    }

    private void aggregate(String jobId, String executionId, String suiteName) {
        this.testSuiteResultResource.aggregateTestSuiteResult(jobId, executionId, suiteName);
        this.testSessionResource.aggregateTestSession(jobId, executionId, SELF);
        this.jobResource.aggregateJob(jobId);
    }

    private Date getMinDate(Date date1, Date date2) {
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        } else {
            if (date1.before(date2)) {
                return date1;
            } else {
                return date2;
            }
        }
    }

    private Date getMaxDate(Date date1, Date date2) {
        if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        } else {
            if (date1.after(date2)) {
                return date1;
            } else {
                return date2;
            }
        }
    }
}

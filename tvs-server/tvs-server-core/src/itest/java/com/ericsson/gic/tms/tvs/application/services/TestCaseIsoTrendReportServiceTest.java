package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.presentation.dto.ContextBean;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Resource;
import com.ericsson.gic.tms.presentation.resources.ContextResource;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.application.util.VersionConverter;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.TestCaseResultTrendReport;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.record.GroupedPassRateRecord;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.JobMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * Created by eniakel on 09/09/2016.
 */
public class TestCaseIsoTrendReportServiceTest extends AbstractIntegrationTest {

    private static final String CONTEXT_ID = "68290196-9874-4b21-ba0a-3472ceb0c5a1";
    private static final String ISO_VERSION_1 = "17.1";
    private static final String ISO_VERSION_2 = "17.2";
    private static final String DROP_1 = "15.6";

    private static final String JOB_NAME_1 = "jobName123";
    private static final String JOB_NAME_2 = "jobName456";
    private static final String ALL_JOBS = JOB_NAME_1 + "," + JOB_NAME_2;

    private static final String NO_FILTER = "";

    private Date startOfTest;

    @Autowired
    private ContextResource contextResource;

    @Autowired
    private JobService jobService;

    @Autowired
    private TestCaseIsoTrendReportService testCaseIsoTrendReportService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private QueryService queryService;

    @Before
    public void setUp() {
        startOfTest = new Date();

        ContextBean contextBean = new ContextBean();
        contextBean.setId(CONTEXT_ID);
        doReturn(new DocumentList<>(newArrayList(new Resource<>(contextBean))))
                .when(contextResource).getChildren(any(String.class));

        mongoFixtures.dropCollection(JOB.getName());
        mongoFixtures.dropCollection(TEST_SESSION.getName());
        mongoFixtures.dropCollection(TEST_SUITE_RESULT.getName());
        mongoFixtures.dropCollection(TEST_CASE_RESULT.getName());
    }

    @Test
    public void trendReportByComponentAllJobs() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(ALL_JOBS);
        Query query = queryService.createQuery(NO_FILTER);

        List<TestCaseResultTrendReport> trendReportByComponent = testCaseIsoTrendReportService.aggregateByTag(
                newArrayList(CONTEXT_ID), jobIds, newArrayList(ISO_VERSION_1),
            COMPONENTS, query);
        List<GroupedPassRateRecord> data = trendReportByComponent.get(0).getData();

        assertThat(data.size()).isEqualTo(3);
        assertThat(data).extracting(GroupedPassRateRecord::getGroupBy)
                .containsOnly("Topology Browser", "License Management", "SSO Utilities");
        assertThat(data.get(0).getGroupBy()).isEqualTo("Topology Browser");
        assertThat(data.get(0).getPassRate()).isEqualTo(100);
        assertThat(data.get(1).getGroupBy()).isEqualTo("License Management");
        assertThat(data.get(1).getPassRate()).isEqualTo(75);
        assertThat(data.get(2).getGroupBy()).isEqualTo("SSO Utilities");
        assertThat(data.get(2).getPassRate()).isEqualTo(16);
    }

    @Test
    public void trendReportByComponentFilteredByJob() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(JOB_NAME_1);
        Query query = queryService.createQuery(NO_FILTER);

        List<TestCaseResultTrendReport> trendReportByComponent = testCaseIsoTrendReportService.aggregateByTag(
                newArrayList(CONTEXT_ID), jobIds, newArrayList(ISO_VERSION_1),
            COMPONENTS, query);
        List<GroupedPassRateRecord> data = trendReportByComponent.get(0).getData();

        assertThat(data.size()).isEqualTo(2);
        assertThat(data).extracting(GroupedPassRateRecord::getGroupBy)
                .containsOnly("License Management", "SSO Utilities");
        assertThat(data.get(0).getGroupBy()).isEqualTo("License Management");
        assertThat(data.get(0).getPassRate()).isEqualTo(66);
        assertThat(data.get(1).getGroupBy()).isEqualTo("SSO Utilities");
        assertThat(data.get(1).getPassRate()).isEqualTo(33);
    }

    @Test
    public void trendReportByPriorityAllJobs() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(ALL_JOBS);
        Query query = queryService.createQuery(NO_FILTER);

        List<TestCaseResultTrendReport> trendReportByComponent = testCaseIsoTrendReportService.aggregateByTag(
                newArrayList(CONTEXT_ID), jobIds, newArrayList(ISO_VERSION_1),
                PRIORITY, query);
        List<GroupedPassRateRecord> data = trendReportByComponent.get(0).getData();

        assertThat(data.size()).isEqualTo(3);
        assertThat(data).extracting(GroupedPassRateRecord::getGroupBy)
                .containsOnly("Minor", "Normal", "Blocker");
        assertThat(data.get(0).getGroupBy()).isEqualTo("Blocker");
        assertThat(data.get(0).getPassRate()).isEqualTo(100);
        assertThat(data.get(1).getGroupBy()).isEqualTo("Normal");
        assertThat(data.get(1).getPassRate()).isEqualTo(33);
        assertThat(data.get(2).getGroupBy()).isEqualTo("Minor");
        assertThat(data.get(2).getPassRate()).isEqualTo(20);
    }

    @Test
    public void trendReportByPriorityFilteredByJob() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(JOB_NAME_1);
        Query query = queryService.createQuery(NO_FILTER);

        List<TestCaseResultTrendReport> trendReportByPriority = testCaseIsoTrendReportService.aggregateByTag(
                newArrayList(CONTEXT_ID), jobIds, newArrayList(ISO_VERSION_1),
                PRIORITY, query);
        List<GroupedPassRateRecord> data = trendReportByPriority.get(0).getData();

        assertThat(data.size()).isEqualTo(2);
        assertThat(data).extracting(GroupedPassRateRecord::getGroupBy)
                .containsOnly("Minor", "Normal");
        assertThat(data.get(0).getGroupBy()).isEqualTo("Normal");
        assertThat(data.get(0).getPassRate()).isEqualTo(50);
        assertThat(data.get(1).getGroupBy()).isEqualTo("Minor");
        assertThat(data.get(1).getPassRate()).isEqualTo(33);
    }

    @Test
    public void trendReportBySuiteAllJobs() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(ALL_JOBS);
        Query query = queryService.createQuery(NO_FILTER);

        List<TestCaseResultTrendReport> trendReportByComponent = testCaseIsoTrendReportService.aggregateByTag(
                newArrayList(CONTEXT_ID), jobIds, newArrayList(ISO_VERSION_1),
                SUITE_NAME, query);
        List<GroupedPassRateRecord> data = trendReportByComponent.get(0).getData();

        assertThat(data.size()).isEqualTo(3);
        assertThat(data).extracting(GroupedPassRateRecord::getGroupBy)
                .containsOnly("suite_1", "suite_2", "suite_3");
        assertThat(data.get(0).getGroupBy()).isEqualTo("suite_3");
        assertThat(data.get(0).getPassRate()).isEqualTo(100);
        assertThat(data.get(1).getGroupBy()).isEqualTo("suite_2");
        assertThat(data.get(1).getPassRate()).isEqualTo(50);
        assertThat(data.get(2).getGroupBy()).isEqualTo("suite_1");
        assertThat(data.get(2).getPassRate()).isEqualTo(20);
    }

    @Test
    public void trendReportBySuiteFilteredByJob() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(JOB_NAME_1);
        Query query = queryService.createQuery(NO_FILTER);

        List<TestCaseResultTrendReport> trendReportByPriority = testCaseIsoTrendReportService.aggregateByTag(
                newArrayList(CONTEXT_ID), jobIds, newArrayList(ISO_VERSION_1),
                SUITE_NAME, query);
        List<GroupedPassRateRecord> data = trendReportByPriority.get(0).getData();

        assertThat(data.size()).isEqualTo(2);
        assertThat(data).extracting(GroupedPassRateRecord::getGroupBy)
                .containsOnly("suite_1", "suite_2");
        assertThat(data.get(0).getGroupBy()).isEqualTo("suite_2");
        assertThat(data.get(0).getPassRate()).isEqualTo(50);
        assertThat(data.get(1).getGroupBy()).isEqualTo("suite_1");
        assertThat(data.get(1).getPassRate()).isEqualTo(33);
    }

    @Test
    public void trendReportByGroupAllJobs() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(ALL_JOBS);
        Query query = queryService.createQuery(NO_FILTER);

        List<TestCaseResultTrendReport> trendReportByComponent = testCaseIsoTrendReportService.aggregateByTag(
                newArrayList(CONTEXT_ID), jobIds, newArrayList(ISO_VERSION_1),
                GROUPS, query);
        List<GroupedPassRateRecord> data = trendReportByComponent.get(0).getData();

        assertThat(data.size()).isEqualTo(3);
        assertThat(data).extracting(GroupedPassRateRecord::getGroupBy)
                .containsOnly("PMIC", "OpenDJ", "NAM TMS");
        assertThat(data.get(0).getGroupBy()).isEqualTo("PMIC");
        assertThat(data.get(0).getPassRate()).isEqualTo(50);
        assertThat(data.get(1).getGroupBy()).isEqualTo("NAM TMS");
        assertThat(data.get(1).getPassRate()).isEqualTo(40);
        assertThat(data.get(2).getGroupBy()).isEqualTo("OpenDJ");
        assertThat(data.get(2).getPassRate()).isEqualTo(28);
    }

    @Test
    public void trendReportByGroupFilteredByJob() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(JOB_NAME_1);
        Query query = queryService.createQuery(NO_FILTER);

        List<TestCaseResultTrendReport> trendReportByPriority = testCaseIsoTrendReportService.aggregateByTag(
                newArrayList(CONTEXT_ID), jobIds, newArrayList(ISO_VERSION_1),
                GROUPS, query);
        List<GroupedPassRateRecord> data = trendReportByPriority.get(0).getData();

        assertThat(data.size()).isEqualTo(2);
        assertThat(data).extracting(GroupedPassRateRecord::getGroupBy)
                .containsOnly("OpenDJ", "NAM TMS");
        assertThat(data.get(0).getGroupBy()).isEqualTo("NAM TMS");
        assertThat(data.get(0).getPassRate()).isEqualTo(33);
        assertThat(data.get(1).getGroupBy()).isEqualTo("OpenDJ");
        assertThat(data.get(1).getPassRate()).isEqualTo(25);
    }

    private void prepareData() {
        JobBean job = job(
                testSessionWithAdditionalData(ISO_VERSION_1,
                    VersionConverter.getPaddedVersion(ISO_VERSION_1),
                    DROP_1,
                        testSuiteResult("suite_1",
                                testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Minor",
                                    newArrayList("OpenDJ"), newArrayList("SSO Utilities", "License Management")),
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Normal",
                                    newArrayList("OpenDJ"), newArrayList("SSO Utilities"))
                        )
                ),
                testSessionWithAdditionalData(ISO_VERSION_1,
                    VersionConverter.getPaddedVersion(ISO_VERSION_1),
                    DROP_1,
                        testSuiteResult("suite_2",
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Minor",
                                    newArrayList("OpenDJ", "NAM TMS"), newArrayList("SSO Utilities")),
                                testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Normal",
                                        newArrayList("NAM TMS"),
                                    newArrayList("License Management"))
                        ),
                        testSuiteResult("suite_1",
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Minor",
                                        newArrayList("NAM TMS", "OpenDJ"),
                                    newArrayList("License Management"))
                        )
                ),
                testSessionWithAdditionalData(ISO_VERSION_2,
                    VersionConverter.getPaddedVersion(ISO_VERSION_2),
                    DROP_1,
                        testSuiteResult("suite_1",
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_2, "Minor",
                                    newArrayList("RFA"), newArrayList("Topology Browser")),
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_2, "Normal",
                                    newArrayList("RFA250"), newArrayList("Topology Browser"))
                        )
                )
        );
        job = jobService.updateJob(CONTEXT_ID, JOB_NAME_1, job);
        mapJobIdToTestCaseResult(job);

        JobBean job2 = job(
                testSessionWithAdditionalData(ISO_VERSION_1,
                    VersionConverter.getPaddedVersion(ISO_VERSION_1),
                    DROP_1,
                        testSuiteResult("suite_1",
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Minor",
                                    newArrayList("PMIC"), newArrayList("SSO Utilities")),
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Normal",
                                    newArrayList("OpenDJ"), newArrayList("SSO Utilities"))
                        )
                ),
                testSessionWithAdditionalData(ISO_VERSION_1,
                    VersionConverter.getPaddedVersion(ISO_VERSION_1),
                    DROP_1,
                        testSuiteResult("suite_2",
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Minor",
                                    newArrayList("PMIC", "OpenDJ", "NAM TMS"), newArrayList("SSO Utilities")),
                                testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Blocker",
                                    newArrayList("PMIC", "NAM TMS"), newArrayList("License Management"))
                        ),
                        testSuiteResult("suite_3",
                                testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Blocker",
                                    newArrayList("PMIC", "OpenDJ"), newArrayList("Topology Browser"))
                        )
                )
        );
        job2 = jobService.updateJob(CONTEXT_ID, JOB_NAME_2, job2);
        mapJobIdToTestCaseResult(job2);
    }

    private TestCaseResultBean testCaseResultWithAdditionalData(String resultCode, String iso, String priority,
                                                                List<String> groups, List<String> components) {
        TestCaseResultBean testCaseResultBean =
                testCaseResult(uniqueString(), resultCode, dateMoment(5), dateMoment(70));

        testCaseResultBean.addAdditionalFields(DROP_NAME, "2.2");
        testCaseResultBean.addAdditionalFields(ISO_VERSION, iso);
        testCaseResultBean.addAdditionalFields(PRIORITY, priority);
        testCaseResultBean.addAdditionalFields(GROUPS, groups);
        testCaseResultBean.addAdditionalFields(COMPONENTS, components);

        return testCaseResultBean;
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

    private void mapJobIdToTestCaseResult(JobBean job) {
        for (TestSessionBean testSession : job.getTestSessions()) {
            addAdditionalFields(testSession, job);
        }
        jobRepository.save(jobMapper.toEntity(job));
    }

    private void addAdditionalFields(TestSessionBean testSession, JobBean job) {
        if (testSession.getTestSuites().isEmpty()) {
            return;
        }
        for (TestCaseResultBean testCaseResult : testSession.getTestSuites().get(0).getTestCaseResults()) {
            testCaseResult.addAdditionalFields(JOB_ID, job.getId());
        }
    }
}

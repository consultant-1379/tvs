package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.JobMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoComponentReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoGroupReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoPriorityReportBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class IsoReportServiceTest extends AbstractIntegrationTest {

    private static final String CONTEXT_ID = "68290196-9874-4b21-ba0a-3472ceb0c5a1";
    private static final String JOB_NAME_1 = "jobName123";
    private static final String JOB_NAME_2 = "jobName456";
    private static final String ALL_JOBS = JOB_NAME_1 + "," + JOB_NAME_2;

    @Autowired
    private JobService jobService;

    @Autowired
    private IsoReportService isoReportService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    private Date startOfTest;

    @Before
    public void setUp() {
        startOfTest = new Date();
        mongoFixtures.dropCollection(JOB.getName());
        mongoFixtures.dropCollection(TEST_SESSION.getName());
        mongoFixtures.dropCollection(TEST_SUITE_RESULT.getName());
        mongoFixtures.dropCollection(TEST_CASE_RESULT.getName());
    }

    @Test
    public void testPriorityAggregation() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(ALL_JOBS);

        List<IsoPriorityReportBean> data =
            isoReportService.aggregateIsoPriorityReport("17.2", jobIds, newArrayList(CONTEXT_ID));

        assertThat(data.size()).isEqualTo(3);
        assertThat(data).extracting(IsoPriorityReportBean::getPriority)
            .containsOnly("Minor", "Normal", "Blocker");

        IsoPriorityReportBean blocker = data.get(0);
        assertThat(blocker.getPriority()).isEqualTo("Blocker");
        assertThat(blocker.getTotal()).isEqualTo(3);
        assertThat(blocker.getPassed()).isEqualTo(2);
        assertThat(blocker.getFailed()).isEqualTo(1);
        assertThat(blocker.getBroken()).isEqualTo(0);
        assertThat(blocker.getCancelled()).isEqualTo(0);
        assertThat(blocker.getPassRate()).isEqualTo(66);
    }

    @Test
    public void testPriorityAggregationFilteredByJob() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(JOB_NAME_1);

        List<IsoPriorityReportBean> data =
                isoReportService.aggregateIsoPriorityReport("17.2", jobIds, newArrayList(CONTEXT_ID));

        assertThat(data.size()).isEqualTo(3);
        assertThat(data).extracting(IsoPriorityReportBean::getPriority)
                .containsOnly("Minor", "Normal", "Blocker");

        IsoPriorityReportBean blocker = data.get(0);
        assertThat(blocker.getPriority()).isEqualTo("Blocker");
        assertThat(blocker.getTotal()).isEqualTo(2);
        assertThat(blocker.getPassed()).isEqualTo(1);
        assertThat(blocker.getFailed()).isEqualTo(1);
        assertThat(blocker.getBroken()).isEqualTo(0);
        assertThat(blocker.getCancelled()).isEqualTo(0);
        assertThat(blocker.getPassRate()).isEqualTo(50);
    }

    @Test
    public void testPriorityAggregationWithNoJobs() {
        prepareData();
        List<String> jobIds = new ArrayList<>();

        List<IsoPriorityReportBean> data =
                isoReportService.aggregateIsoPriorityReport("17.2", jobIds, newArrayList(CONTEXT_ID));
        assertThat(data.size()).isEqualTo(0);
    }


    @Test
    public void testGroupAggregation() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(ALL_JOBS);

        List<IsoGroupReportBean> data =
            isoReportService.aggregateIsoGroupReport("17.2", jobIds, newArrayList(CONTEXT_ID));

        assertThat(data).extracting(IsoGroupReportBean::getGroup)
            .containsOnly("PMIC", "OpenDJ", "NAM TMS");

        IsoGroupReportBean namTms = data.get(0);
        assertThat(namTms.getGroup()).isEqualTo("NAM TMS");
        assertThat(namTms.getTotal()).isEqualTo(4);
        assertThat(namTms.getPassed()).isEqualTo(2);
        assertThat(namTms.getFailed()).isEqualTo(2);
        assertThat(namTms.getBroken()).isEqualTo(0);
        assertThat(namTms.getCancelled()).isEqualTo(0);
        assertThat(namTms.getPassRate()).isEqualTo(50);
    }

    @Test
    public void testGroupAggregationFilteredByJob() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(JOB_NAME_1);

        List<IsoGroupReportBean> data =
                isoReportService.aggregateIsoGroupReport("17.2", jobIds, newArrayList(CONTEXT_ID));

        assertThat(data).extracting(IsoGroupReportBean::getGroup)
                .containsOnly("PMIC", "OpenDJ", "NAM TMS");

        IsoGroupReportBean namTms = data.get(0);
        assertThat(namTms.getGroup()).isEqualTo("NAM TMS");
        assertThat(namTms.getTotal()).isEqualTo(3);
        assertThat(namTms.getPassed()).isEqualTo(2);
        assertThat(namTms.getFailed()).isEqualTo(1);
        assertThat(namTms.getBroken()).isEqualTo(0);
        assertThat(namTms.getCancelled()).isEqualTo(0);
        assertThat(namTms.getPassRate()).isEqualTo(66);
    }

    @Test
    public void testGroupAggregationWithNoJobs() {
        prepareData();
        List<String> jobIds = new ArrayList<>();

        List<IsoGroupReportBean> data =
                isoReportService.aggregateIsoGroupReport("17.2", jobIds, newArrayList(CONTEXT_ID));
        assertThat(data.size()).isEqualTo(0);
    }

    @Test
    public void testComponentAggregation() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(ALL_JOBS);

        List<IsoComponentReportBean> data =
            isoReportService.aggregateIsoComponentReport("17.2", jobIds, newArrayList(CONTEXT_ID));

        assertThat(data).extracting(IsoComponentReportBean::getComponent)
            .containsOnly("SSO Utilities", "License Management");

        IsoComponentReportBean licenseManagement = data.get(0);

        assertThat(licenseManagement.getComponent()).isEqualTo("License Management");
        assertThat(licenseManagement.getTotal()).isEqualTo(6);
        assertThat(licenseManagement.getPassed()).isEqualTo(4);
        assertThat(licenseManagement.getFailed()).isEqualTo(2);
        assertThat(licenseManagement.getBroken()).isEqualTo(0);
        assertThat(licenseManagement.getCancelled()).isEqualTo(0);
        assertThat(licenseManagement.getPassRate()).isEqualTo(66);
    }

    @Test
    public void testComponentAggregationFilteredByJob() {
        prepareData();
        List<String> jobIds = jobService.findJobIdsFromNames(JOB_NAME_1);

        List<IsoComponentReportBean> data =
                isoReportService.aggregateIsoComponentReport("17.2", jobIds, newArrayList(CONTEXT_ID));

        assertThat(data).extracting(IsoComponentReportBean::getComponent)
                .containsOnly("SSO Utilities", "License Management");

        IsoComponentReportBean licenseManagement = data.get(0);

        assertThat(licenseManagement.getComponent()).isEqualTo("License Management");
        assertThat(licenseManagement.getTotal()).isEqualTo(4);
        assertThat(licenseManagement.getPassed()).isEqualTo(3);
        assertThat(licenseManagement.getFailed()).isEqualTo(1);
        assertThat(licenseManagement.getBroken()).isEqualTo(0);
        assertThat(licenseManagement.getCancelled()).isEqualTo(0);
        assertThat(licenseManagement.getPassRate()).isEqualTo(75);
    }

    @Test
    public void testComponentAggregationFilteredWithNoJobs() {
        prepareData();
        List<String> jobIds = newArrayList();

        List<IsoComponentReportBean> data =
                isoReportService.aggregateIsoComponentReport("17.2", jobIds, newArrayList(CONTEXT_ID));
        assertThat(data.size()).isEqualTo(0);
    }

    private void prepareData() {
        String executionId1 = uniqueString();
        String executionId2 = uniqueString();
        String executionId3 = uniqueString();
        String executionId4 = uniqueString();
        String executionId5 = uniqueString();

        JobBean job = job(
            testSession(executionId1,
                testSuiteResult(
                    testCaseResultWithAdditionalData(PASSED, "Minor", newArrayList("PMIC"),
                        newArrayList("SSO Utilities", "License Management")),
                    testCaseResultWithAdditionalData(FAILED, "Normal", newArrayList("OpenDJ"),
                        newArrayList("SSO Utilities"))
                )
            ),
            testSession(executionId2,
                testSuiteResult(
                    testCaseResultWithAdditionalData(FAILED, "Minor", newArrayList("PMIC", "OpenDJ", "NAM TMS"),
                        newArrayList("SSO Utilities")),
                    testCaseResultWithAdditionalData(PASSED, "Blocker", newArrayList("PMIC", "NAM TMS"),
                        newArrayList("License Management"))
                ),
                testSuiteResult(
                    testCaseResultWithAdditionalData(FAILED, "Blocker", newArrayList("PMIC", "OpenDJ"),
                        newArrayList("License Management"))
                )
            ),
            testSession(executionId3,
                testSuiteResult(
                    testCaseResultWithAdditionalData(PASSED, "Minor", newArrayList("NAM TMS"),
                        newArrayList("License Management"))
                )
            ),
            testSession(executionId3,
                testSuiteResult(
                    testCaseResult(uniqueString(), PASSED)
                )
            )
        );

        job = jobService.updateJob(CONTEXT_ID, JOB_NAME_1, job);
        mapJobIdToTestCaseResult(job);

        JobBean job2 = job(
                testSession(executionId4,
                        testSuiteResult(
                                testCaseResultWithAdditionalData(FAILED, "Normal",
                                    newArrayList("PMIC"), newArrayList("SSO Utilities")),
                                testCaseResultWithAdditionalData(FAILED, "Normal",
                                    newArrayList("OpenDJ"), newArrayList("SSO Utilities"))
                        )
                ),
                testSession(executionId5,
                        testSuiteResult(
                                testCaseResultWithAdditionalData(PASSED, "Minor", newArrayList("PMIC"),
                                    newArrayList("SSO Utilities")),
                                testCaseResultWithAdditionalData(FAILED, "Minor", newArrayList("PMIC", "NAM TMS"),
                                    newArrayList("License Management"))
                        ),
                        testSuiteResult(
                                testCaseResultWithAdditionalData(PASSED, "Blocker", newArrayList("OpenDJ"),
                                    newArrayList("License Management"))
                        )
                )
        );

        job2 = jobService.updateJob(CONTEXT_ID, JOB_NAME_2, job2);
        mapJobIdToTestCaseResult(job2);
    }

    private TestCaseResultBean testCaseResultWithAdditionalData(String resultCode, String priority,
                                                                List<String> groups, List<String> components) {
        TestCaseResultBean testCaseResultBean =
            testCaseResult(uniqueString(), resultCode, dateMoment(1), dateMoment(10));
        testCaseResultBean.addAdditionalFields(DROP_NAME, "1.2");
        testCaseResultBean.addAdditionalFields(ISO_VERSION, "17.2");
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

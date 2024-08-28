package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.presentation.dto.ContextBean;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Meta;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Resource;
import com.ericsson.gic.tms.presentation.resources.ContextResource;
import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.application.services.JobService;
import com.ericsson.gic.tms.tvs.application.util.VersionConverter;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.JobMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.GroupedPassRateBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoCsvReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoPriorityReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.ReportType;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.TestCaseIsoTrendBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.TestCaseIsoTrendReport;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.reports.ReportType.*;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

public class TestCaseReportControllerTest extends AbstractIntegrationTest {

    private static final String CONTEXT_ID = "68290196-9874-4b21-ba0a-3472ceb0c5a1";

    private static final String ISO_VERSION_1 = "17.1";
    private static final String ISO_VERSION_2 = "17.2";
    private static final String ISO_VERSION_3 = "17.3";
    private static final String ISO_VERSION_IGNORED = "17.4";
    private static final String ISO_VERSION_11 = "17.11";
    private static final String ISO_VERSION_12 = "17.12";
    private static final String DROP_1 = "15.2";

    private static final String JOB_DETAILS = "job-details";

    private static final String JOB_NAME_1 = "jobName123";
    private static final String JOB_NAME_2 = "jobName456";
    private static final String ALL_JOBS = JOB_NAME_1 + "," + JOB_NAME_2;

    private static final String NO_FILTER = "";
    private static final String GROUP_FILTER = "groupBy~string|%s";
    private static final String RESULT_CODE_FILTER = "resultCode~string|[%s]";

    @Autowired
    private TestCaseReportController controller;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private ContextResource contextResource;

    @Autowired
    private JobRepository jobRepository;

    private Date startOfTest;

    @Before
    public void setUp() {
        startOfTest = new Date();

        ContextBean contextBean = new ContextBean();
        contextBean.setId(CONTEXT_ID);
        doReturn(new DocumentList<>(newArrayList(new Resource<>(contextBean))))
            .when(contextResource).getChildren(CONTEXT_ID);

        mongoFixtures.dropCollection(JOB.getName());
        mongoFixtures.dropCollection(TEST_SESSION.getName());
        mongoFixtures.dropCollection(TEST_SUITE_RESULT.getName());
        mongoFixtures.dropCollection(TEST_CASE_RESULT.getName());
        controller.setJsonApiUtil(mockedJsonApiUtil);
    }

    @Test
    public void isoVersions() {
        prepareData();
        DocumentList<?> report = controller.getReport(
                CONTEXT_ID, ISO_PRIORITY, ISO_VERSION_1, null, 0, ALL_JOBS, NO_FILTER);
        List<String> isoVersions = (List<String>) report.getMeta().get(Meta.ISO_VERSIONS);
        assertThat(isoVersions)
            .as("List of all ISO versions")
            .containsExactly(ISO_VERSION_12, ISO_VERSION_11, ISO_VERSION_3, ISO_VERSION_2, ISO_VERSION_1);

        List<String> jobNames = (List<String>) report.getMeta().get(JOB_DETAILS);
        assertThat(jobNames)
                .as("List of all Job Names by Context")
                .contains(JOB_NAME_1, JOB_NAME_2);
    }

    @Test
    public void isoVersionsNotPresent() {
        DocumentList<?> report = controller.getReport(CONTEXT_ID, ISO_PRIORITY, null, null, 0, ALL_JOBS, NO_FILTER);

        assertThat(report.unwrap())
            .isEmpty();
        List<String> isoVersions = (List<String>) report.getMeta().get(Meta.ISO_VERSIONS);
        assertThat(isoVersions).isEmpty();

        List<String> jobNames = (List<String>) report.getMeta().get(JOB_DETAILS);
        assertThat(jobNames).isEmpty();
    }

    @Test
    public void defaultReportForLatestIso() {
        prepareData();

        List<IsoPriorityReportBean> report =
            (List<IsoPriorityReportBean>) controller.getReport(
                    CONTEXT_ID, ISO_PRIORITY, null, null, 0, ALL_JOBS, NO_FILTER)
                    .unwrap();

        assertThat(report.size()).isEqualTo(1);
        assertThat(report.get(0).getPriority()).isEqualTo("Minor");
    }

    @Test
    public void getTrendComponentReportOfAllIsoVersions() {
        prepareData();

        DocumentList response = controller.getReport(CONTEXT_ID, TREND_COMPONENT, null, null, 0, ALL_JOBS, NO_FILTER);

        assertThat(response)
            .as("Trend Component Response")
            .isNotNull();
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getData()).isNotEmpty().hasSize(4);
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getMeta()).isNotEmpty();
        assertThat(response.getMeta().getMeta()).containsKey(Meta.ISO_VERSIONS);

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();
        assertThat(reportData)
            .as("Trend Component report")
            .hasSize(4)
            .isSortedAccordingTo(new TestCaseIsoAscComparator());
        assertThat(reportData)
            .as("Trend Component Drop Name")
            .extracting(TestCaseIsoTrendBean::getDropName)
            .contains("2.2");
        assertThat(reportData)
            .as("Trend Component ISO version")
            .extracting(TestCaseIsoTrendBean::getIsoVersion)
            .containsExactly(ISO_VERSION_1, ISO_VERSION_2, ISO_VERSION_3, ISO_VERSION_11);
        assertThat(reportData)
            .as("Trend Component Data Size")
            .extracting(TestCaseIsoTrendBean::getData)
            .hasSize(4);

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Component Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("License Management");
        assertThat(groupedPassRateBeans2)
            .as("Trend Component Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("TAF TE");
        assertThat(groupedPassRateBeans1)
            .as("Trend Component Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(50, 33);
        assertThat(groupedPassRateBeans2)
            .as("Trend Component Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(100);
    }

    @Test
    public void getTrendPriorityReportOfAllIsoVersionsFilteredByJob() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_PRIORITY, null, null, 0, JOB_NAME_1, NO_FILTER);

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();
        assertThat(reportData)
            .as("Trend Priority report")
            .hasSize(4)
            .isSortedAccordingTo(new TestCaseIsoAscComparator());

        assertThat(reportData)
            .as("Trend Priority Drop Name")
            .extracting(TestCaseIsoTrendBean::getDropName)
            .contains("2.2")
            .doesNotContainNull();
        assertThat(reportData)
            .as("Trend Priority ISO version")
            .extracting(TestCaseIsoTrendBean::getIsoVersion)
            .containsExactly(ISO_VERSION_1, ISO_VERSION_2, ISO_VERSION_3, ISO_VERSION_11);
        assertThat(reportData)
            .as("Trend Priority Data Size")
            .extracting(TestCaseIsoTrendBean::getData)
            .hasSize(4);

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Priority Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("Blocker", "Normal", "Minor");
        assertThat(groupedPassRateBeans2)
            .as("Trend Priority Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("Normal", "Minor");
        assertThat(groupedPassRateBeans1)
            .as("Trend Priority Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(50, 0, 66);
        assertThat(groupedPassRateBeans2)
            .as("Trend Priority Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(0, 50);
    }

    @Test
    public void getTrendPriorityReportOfAllIsoVersions() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_PRIORITY, null, null, 0, ALL_JOBS, NO_FILTER);

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Priority Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("Blocker", "Normal", "Minor");
        assertThat(groupedPassRateBeans2)
            .as("Trend Priority Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("Normal", "Minor");
        assertThat(groupedPassRateBeans1)
            .as("Trend Priority Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(75, 0, 60);
        assertThat(groupedPassRateBeans2)
            .as("Trend Priority Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(0, 50);
    }

    @Test
    public void getTrendGroupReportOfAllIsoVersions() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_GROUP, null, null, 0, ALL_JOBS, NO_FILTER);

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();
        assertThat(reportData)
            .as("Trend Group report")
            .hasSize(4)
            .isSortedAccordingTo(new TestCaseIsoAscComparator());

        assertThat(reportData)
            .as("Trend Group Drop Name")
            .extracting(TestCaseIsoTrendBean::getDropName)
            .contains("2.2");
        assertThat(reportData)
            .as("Trend Group ISO version")
            .extracting(TestCaseIsoTrendBean::getIsoVersion)
            .containsExactly(ISO_VERSION_1, ISO_VERSION_2, ISO_VERSION_3, ISO_VERSION_11);
        assertThat(reportData)
            .as("Trend Group Data Size")
            .extracting(TestCaseIsoTrendBean::getData)
            .hasSize(4);

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("RFA", "RFA250", "NAM TMS", "OpenDJ", "PMIC");

        assertThat(groupedPassRateBeans2)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("TAF");

        assertThat(groupedPassRateBeans1)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(100, 0, 75, 33, 62);
        assertThat(groupedPassRateBeans2)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(0, 0, 100);
    }

    @Test
    public void getTrendGroupReportOfAllIsoVersionsFilteredByJob() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_GROUP, null, null, 0, JOB_NAME_1, NO_FILTER);

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("RFA", "RFA250", "NAM TMS", "OpenDJ", "PMIC");

        assertThat(groupedPassRateBeans2)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("RFA", "RFA250", "TAF");

        assertThat(groupedPassRateBeans1)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(100, 0, 50, 0, 50);
        assertThat(groupedPassRateBeans2)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(0, 0, 100);
    }

    @Test
    public void getTrendSuiteReportOfAllIsoVersionsFilteredByJob() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_SUITE, null, null, 0, JOB_NAME_1, NO_FILTER);

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Suite Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("suite_1", "suite_2");

        assertThat(groupedPassRateBeans2)
            .as("Trend Suite Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("suite_3");

        assertThat(groupedPassRateBeans1)
            .as("Trend Suite Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(50, 0, 50);
        assertThat(groupedPassRateBeans2)
            .as("Trend Suite Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(0, 100);
    }

    @Test
    public void getTrendSuiteReportOfAllIsoVersions() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_SUITE, null, null, 0, ALL_JOBS, NO_FILTER);

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();
        assertThat(reportData)
            .as("Trend Suite report")
            .hasSize(4)
            .isSortedAccordingTo(new TestCaseIsoAscComparator());

        assertThat(reportData)
            .as("Trend Suite Drop Name")
            .extracting(TestCaseIsoTrendBean::getDropName)
            .contains("2.2");
        assertThat(reportData)
            .as("Trend Suite ISO version")
            .extracting(TestCaseIsoTrendBean::getIsoVersion)
            .containsExactly(ISO_VERSION_1, ISO_VERSION_2, ISO_VERSION_3, ISO_VERSION_11);
        assertThat(reportData)
            .as("Trend Suite Data Size")
            .extracting(TestCaseIsoTrendBean::getData)
            .hasSize(4);

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Suite Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("suite_1", "suite_2");

        assertThat(groupedPassRateBeans2)
            .as("Trend Suite Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).contains("suite_3");

        assertThat(groupedPassRateBeans1)
            .as("Trend Suite Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(50, 50, 50);
        assertThat(groupedPassRateBeans2)
            .as("Trend Suite Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).contains(0, 100);
    }

    @Test
    public void getTrendGroupReportFilteredByGroup() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_GROUP, null, null, 0, ALL_JOBS,
            format(GROUP_FILTER, "TAF"));

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();
        assertThat(reportData)
            .as("Trend Group report")
            .hasSize(2)
            .isSortedAccordingTo(new TestCaseIsoAscComparator());

        assertThat(reportData)
            .as("Trend Group Drop Name")
            .extracting(TestCaseIsoTrendBean::getDropName)
            .contains("2.2");
        assertThat(reportData)
            .as("Trend Group ISO version")
            .extracting(TestCaseIsoTrendBean::getIsoVersion)
            .containsExactly(ISO_VERSION_2, ISO_VERSION_3);
        assertThat(reportData)
            .as("Trend Group Data Size")
            .extracting(TestCaseIsoTrendBean::getData)
            .hasSize(2);

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).containsExactly("TAF");

        assertThat(groupedPassRateBeans2)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).containsExactly("TAF");

        assertThat(groupedPassRateBeans1)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).containsExactly(100);
        assertThat(groupedPassRateBeans2)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).containsExactly(100);
    }

    @Test
    public void getTrendGroupReportFilteredByResultCodeAndJob() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_GROUP, null, null, 0, JOB_NAME_1,
            format(RESULT_CODE_FILTER, FAILED));

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();
        assertThat(reportData)
            .as("Trend Group report")
            .hasSize(2)
            .isSortedAccordingTo(new TestCaseIsoAscComparator());

        assertThat(reportData)
            .as("Trend Group Drop Name")
            .extracting(TestCaseIsoTrendBean::getDropName)
            .contains("2.2");
        assertThat(reportData)
            .as("Trend Group ISO version")
            .extracting(TestCaseIsoTrendBean::getIsoVersion)
            .containsExactly(ISO_VERSION_1, ISO_VERSION_2);
        assertThat(reportData)
            .as("Trend Group Data Size")
            .extracting(TestCaseIsoTrendBean::getData)
            .hasSize(2);

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).containsOnly("RFA250", "OpenDJ", "PMIC");

        assertThat(groupedPassRateBeans2)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).containsExactly("RFA250");

        assertThat(groupedPassRateBeans1)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).containsOnly(0, 0, 50);
        assertThat(groupedPassRateBeans2)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).containsExactly(0);
    }

    @Test
    public void getTrendGroupReportFilteredByResultCode() {
        prepareData();
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_GROUP, null, null, 0, ALL_JOBS,
                format(RESULT_CODE_FILTER, FAILED));

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();

        List<GroupedPassRateBean> groupedPassRateBeans1 = reportData.get(0).getData();
        List<GroupedPassRateBean> groupedPassRateBeans2 = reportData.get(1).getData();
        assertThat(groupedPassRateBeans1)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).containsOnly("RFA250", "OpenDJ", "PMIC");
        assertThat(groupedPassRateBeans2)
            .as("Trend Group Group By field")
            .extracting(GroupedPassRateBean::getGroupBy).containsExactly("RFA250");

        assertThat(groupedPassRateBeans1)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).containsOnly(0, 33, 62);
        assertThat(groupedPassRateBeans2)
            .as("Trend Group Pass Rate")
            .extracting(GroupedPassRateBean::getPassRate).containsExactly(0);
    }

    @Test
    public void getTrendGroupReportOfSpecificIsoVersions() {
        prepareData();
        String fromIsoVersion = ISO_VERSION_1;
        String toIsoVersion = ISO_VERSION_1;
        DocumentList response = controller.getReport(CONTEXT_ID, TREND_GROUP, fromIsoVersion, toIsoVersion, 0, ALL_JOBS,
            NO_FILTER);

        assertThat(response)
            .as("Trend Group Response")
            .isNotNull();
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getMeta()).isNotEmpty();

        List<TestCaseIsoTrendBean> reportData = (List<TestCaseIsoTrendBean>) response.unwrap();
        assertThat(reportData)
            .as("Trend Group report")
            .hasSize(1);
        assertThat(reportData).extracting(TestCaseIsoTrendBean::getIsoVersion).contains(fromIsoVersion);
    }

    @Test
    public void exportIsoPriorityReport() {
        prepareData();
        List<IsoCsvReport> data = controller.exportIsoReports(CONTEXT_ID, ALL_JOBS, ISO_PRIORITY, ISO_VERSION_1)
            .unwrap();

        assertThat(data).hasSize(3);
        assertThat(data).extracting(IsoCsvReport::getId)
            .containsOnly("Minor", "Normal", "Blocker");
        assertThat(data.get(0).getId()).isEqualTo("Blocker");
        assertThat(data.get(0).getPassRate()).isEqualTo(75);
    }

    @Test
    public void exportIsoComponentReport() {
        prepareData();
        List<IsoCsvReport> data = controller.exportIsoReports(CONTEXT_ID, ALL_JOBS, ISO_COMPONENT, ISO_VERSION_1)
            .unwrap();

        assertThat(data).hasSize(3);
        assertThat(data).extracting(IsoCsvReport::getId)
            .containsOnly("License Management", "SSO Utilities", "Topology Browser");
        assertThat(data.get(0).getId()).isEqualTo("License Management");
        assertThat(data.get(0).getPassRate()).isEqualTo(75);
    }

    @Test
    public void exportIsoGroupReport() {
        prepareData();
        List<IsoCsvReport> data = controller.exportIsoReports(CONTEXT_ID, JOB_NAME_2, ISO_GROUP, ISO_VERSION_1)
            .unwrap();

        assertThat(data).hasSize(3);
        assertThat(data).extracting(IsoCsvReport::getId)
            .containsOnly("NAM TMS", "OpenDJ", "PMIC");
        assertThat(data.get(0).getId()).isEqualTo("NAM TMS");
        assertThat(data.get(0).getPassRate()).isEqualTo(100);
    }

    @Test
    public void exportTrendComponentReport() {
        prepareData();
        List<TestCaseIsoTrendReport> data =
            controller.exportTrendReports(CONTEXT_ID, ALL_JOBS, TREND_COMPONENT, null, null).unwrap();

        assertThat(data).hasSize(7);
        assertThat(data).extracting(TestCaseIsoTrendReport::getId)
            .containsOnly("License Management", "SSO Utilities", "Topology Browser", "TAF TE");
        assertThat(data.get(0).getId()).isEqualTo("Topology Browser");
        assertThat(data.get(0).getPassRate()).isEqualTo(50);
        assertThat(data.get(0).getDropName()).isEqualTo("2.2");
        assertThat(data.get(0).getIsoVersion()).isEqualTo("17.1");
    }

    @Test
    public void wrongReportType() {
        prepareData();

        assertThatThrownBy(() -> controller.getReport(CONTEXT_ID, ReportType.DROP_CHART,
                ISO_VERSION_1, null, 0, ALL_JOBS, NO_FILTER))
            .isInstanceOf(NotFoundException.class);
    }

    private void prepareData() {
        JobBean job = job(
            testSessionWithAdditionalData(ISO_VERSION_1,
                VersionConverter.getPaddedVersion(ISO_VERSION_1),
                DROP_1,
                testSuiteResult("suite_1",
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Minor",
                        newArrayList("PMIC"), newArrayList("SSO Utilities")),
                    testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Normal",
                        newArrayList("OpenDJ"), newArrayList("SSO Utilities"))
                )
            ),
            testSessionWithAdditionalData(ISO_VERSION_1,
                VersionConverter.getPaddedVersion(ISO_VERSION_1),
                DROP_1,
                testSuiteResult("suite_1",
                    testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Minor",
                        newArrayList("PMIC", "OpenDJ", "NAM TMS"), newArrayList("SSO Utilities")),
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Blocker", newArrayList("PMIC", "NAM TMS"),
                        newArrayList("License Management"))
                ),
                testSuiteResult("suite_2",
                    testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Blocker", newArrayList("PMIC", "OpenDJ"),
                        newArrayList("License Management"))
                )
            ),
            testSessionWithAdditionalData(ISO_VERSION_3,
                VersionConverter.getPaddedVersion(ISO_VERSION_3),
                DROP_1,
                testSuiteResult("suite_3",
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_3, "Minor", newArrayList("TAF"),
                        newArrayList("TAF TE"))
                )
            ),
            testSessionWithAdditionalData(ISO_VERSION_2,
                VersionConverter.getPaddedVersion(ISO_VERSION_2),
                DROP_1,
                testSuiteResult("suite_3",
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_2, "Minor", newArrayList("TAF"),
                        newArrayList("TAF TE"))
                )
            ),
            testSessionWithAdditionalData(ISO_VERSION_11,
                VersionConverter.getPaddedVersion(ISO_VERSION_11),
                DROP_1,
                testSuiteResult("suite_4",
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_11, "Minor", newArrayList("NAM TMS"),
                        newArrayList("License Management")),
                    testCaseResult(uniqueString(), PASSED)
                )
            ),
            testSessionWithAdditionalData(ISO_VERSION_12,
                VersionConverter.getPaddedVersion(ISO_VERSION_12),
                DROP_1,
                testSuiteResult("suite_4",
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_12, "Minor", newArrayList("NAM TMS"),
                        newArrayList("TAF TE")),
                    testCaseResult(uniqueString(), PASSED)
                )
            ),
            testSessionWithAdditionalData(ISO_VERSION_1,
                VersionConverter.getPaddedVersion(ISO_VERSION_1),
                DROP_1,
                testSuiteResult("suite_5",
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Minor",
                        newArrayList("RFA"), newArrayList("Topology Browser")),
                    testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Normal",
                        newArrayList("RFA250"), newArrayList("Topology Browser"))
                )
            ),
            testSessionWithAdditionalData(ISO_VERSION_2,
                VersionConverter.getPaddedVersion(ISO_VERSION_2),
                DROP_1,
                testSuiteResult("suite_5",
                    testCaseResultWithAdditionalData(FAILED, ISO_VERSION_2, "Minor",
                        newArrayList("RFA"), newArrayList("Topology Browser")),
                    testCaseResultWithAdditionalData(FAILED, ISO_VERSION_2, "Normal",
                        newArrayList("RFA250"), newArrayList("Topology Browser"))
                )
            ),
            testSessionWithIgnored(ISO_VERSION_IGNORED,
                VersionConverter.getPaddedVersion(ISO_VERSION_IGNORED),
                DROP_1,
                testSuiteResult("suite_5",
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_IGNORED, "Minor",
                        newArrayList("RFA"), newArrayList("Topology Browser")),
                    testCaseResultWithAdditionalData(PASSED, ISO_VERSION_IGNORED, "Normal",
                        newArrayList("RFA250"), newArrayList("Topology Browser"))
                )
            )
        );
        job = jobService.updateJob(CONTEXT_ID, JOB_NAME_1, job);
        mapJobIdToTestCaseResult(job);

        JobBean job2 = job(
                testSessionWithAdditionalData(ISO_VERSION_1,
                    VersionConverter.getPaddedVersion(ISO_VERSION_IGNORED),
                    DROP_1,
                        testSuiteResult("suite_1",
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Minor",
                                    newArrayList("PMIC"), newArrayList("SSO Utilities")),
                                testCaseResultWithAdditionalData(FAILED, ISO_VERSION_1, "Normal",
                                    newArrayList("OpenDJ"), newArrayList("SSO Utilities"))
                        )
                ),
                testSessionWithAdditionalData(ISO_VERSION_1,
                    VersionConverter.getPaddedVersion(ISO_VERSION_IGNORED),
                    DROP_1,
                        testSuiteResult("suite_1",
                                testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Minor",
                                    newArrayList("PMIC", "OpenDJ", "NAM TMS"), newArrayList("SSO Utilities")),
                                testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Blocker",
                                    newArrayList("PMIC", "NAM TMS"), newArrayList("License Management"))
                        ),
                        testSuiteResult("suite_2",
                                testCaseResultWithAdditionalData(PASSED, ISO_VERSION_1, "Blocker",
                                    newArrayList("PMIC", "OpenDJ"), newArrayList("License Management"))
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

        if (Objects.equals(iso, ISO_VERSION_12)) {
            testCaseResultBean.addAdditionalFields(DROP_NAME, null);
        } else {
            testCaseResultBean.addAdditionalFields(DROP_NAME, "2.2");
        }

        testCaseResultBean.addAdditionalFields(ISO_VERSION, iso);
        testCaseResultBean.addAdditionalFields(ISO_VERSION_PADDED, VersionConverter.getPaddedVersion(iso));
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

    private class TestCaseIsoAscComparator implements Comparator<TestCaseIsoTrendBean> {

        public int compare(TestCaseIsoTrendBean isoTrend1, TestCaseIsoTrendBean isoTrend2) {
            if (isoTrend1 == null) {
                if (isoTrend2 == null) {
                    return 0;
                }
                return -1;
            } else if (isoTrend2 == null) {
                return 1;
            }
            DefaultArtifactVersion versionObj1 = new DefaultArtifactVersion(isoTrend1.getIsoVersion());
            DefaultArtifactVersion versionObj2 = new DefaultArtifactVersion(isoTrend2.getIsoVersion());
            return versionObj1.compareTo(versionObj2);
        }
    }
}

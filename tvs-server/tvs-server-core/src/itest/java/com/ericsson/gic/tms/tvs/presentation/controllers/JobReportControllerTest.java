package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportType;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.JobCumulativeDropReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.JobLastISODropReportBean;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.*;
import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.*;
import static com.ericsson.gic.tms.presentation.validation.CommonServiceError.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.reports.ReportType.*;
import static org.assertj.core.api.Assertions.*;

public class JobReportControllerTest extends AbstractIntegrationTest {

    private static final String UNKNOWN_JOB_ID = "unknown";
    private static final String JOB_ID = "e215b7d8e4b03ebcdc2b0819";
    private static final String DROP_NAME_16_3 = "16.3";
    private static final String DROP_NAME_16_4 = "16.4";
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2016, 2, 1, 10, 30, 58);
    private static final String DEFAULT_ORDER_BY = "latestIsoVersion";
    private static final int PAGE = 1;
    private static final int SIZE = 20;
    public static final String QUERY = null;

    @Autowired
    private JobReportController controller;

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);
    }

    @Test
    public void getCumulativeDropReportPaginated() {
        DocumentList<? extends DropReportBean> response = controller.getData(JOB_ID, DROP_TABLE.getName(),
            DEFAULT_SINCE, null, PAGE, SIZE, DEFAULT_ORDER_BY, ASC, QUERY, DropReportType.CUMULATIVE);

        assertThat(response).isNotNull();
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getMeta())
            .contains(entry(TOTAL_ITEMS, 2L), entry(ITEM_PER_PAGE, 20), entry(CURRENT_PAGE, 1));

        List<? extends DropReportBean> result = response.unwrap();
        assertThat(result)
            .as("Cumulative Drop Reports")
            .hasSize(2)
            .doesNotContainNull()
            .doesNotHaveDuplicates();
    }

    @Test
    public void getDefaultCumulativeDropReportDataForChart() {
        LocalDateTime until = LocalDateTime.of(2016, 2, 6, 12, 15, 3);

        DocumentList<? extends DropReportBean> response = controller.getData(JOB_ID, DROP_CHART.getName(),
            DEFAULT_SINCE, until, 1, 1, null, null, QUERY, DropReportType.CUMULATIVE);

        assertThat(response)
            .as("Response")
            .isNotNull();
        assertThat(response.getErrors())
            .as("Response errors")
            .isNullOrEmpty();
        assertThat(response.getMeta())
            .as("Response Meta")
            .isNull();
        assertThat(response.getData())
            .as("Response Data")
            .isNotNull();

        List<? extends DropReportBean> result = response.unwrap();
        assertThat(result)
            .as("Drop Reports")
            .hasSize(2)
            .doesNotContainNull()
            .doesNotHaveDuplicates();

        assertThat(result)
            .extracting(DropReportBean::getId)
            .as("Drop Name")
            .containsExactly("1.2", "1.3");

        assertThat(result)
            .extracting(DropReportBean::getIsoLastStartTime)
            .as("ISO last start date")
            .isNotNull();

        assertThat(result)
            .extracting(DropReportBean::getLatestIsoVersion)
            .as("Latest ISO version")
            .containsExactly("1.18.42", "1.18.41");

        assertThat(result)
            .extracting(DropReportBean::getPassRate)
            .as("Pass Rate")
            .containsExactly(0, 50);

        assertThat(result)
            .extracting(DropReportBean::getTestCasesCount)
            .as("Test Case count")
            .containsExactly(1, 2);

        assertThat((List<JobCumulativeDropReportBean>) result)
            .extracting(JobCumulativeDropReportBean::getPassedTestCaseCount)
            .as("Passed Test Case count")
            .containsExactly(0, 1);

        assertThat((List<JobCumulativeDropReportBean>) result)
            .extracting(JobCumulativeDropReportBean::getFailedTestCaseCount)
            .as("Failed Test Case count")
            .containsExactly(1, 0);

        assertThat(result)
            .extracting(DropReportBean::getTestSessionsCount)
            .as("Test Session count")
            .contains(2, 2);

        assertThat(result).as("Asc sort of Drop Report results by Drop name")
            .isSortedAccordingTo((dropBean1, dropBean2) -> {
                DefaultArtifactVersion version1 = new DefaultArtifactVersion(dropBean1.getId());
                DefaultArtifactVersion version2 = new DefaultArtifactVersion(dropBean2.getId());
                return version1.compareTo(version2);
            });
    }

    @Test
    public void getPaginatedDropReportDataForTable() {
        LocalDateTime until = null;
        DocumentList<? extends DropReportBean> response = controller.getData(JOB_ID, DROP_TABLE.getName(),
            DEFAULT_SINCE, until, PAGE, SIZE, DEFAULT_ORDER_BY, ASC, QUERY, DropReportType.LAST_ISO);

        assertThat(response).isNotNull();
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getMeta())
            .contains(entry(TOTAL_ITEMS, 2L), entry(ITEM_PER_PAGE, 20), entry(CURRENT_PAGE, 1));

        List<? extends DropReportBean> result = response.unwrap();
        assertThat(result)
            .as("Drop Reports")
            .hasSize(2)
            .doesNotContainNull()
            .doesNotHaveDuplicates();
    }

    @Test
    public void doesNotAllowDropReportWithZeroIndexPageRequest() {
        LocalDateTime since = null;
        LocalDateTime until = null;
        String orderBy = null;
        SortingMode orderMode = null;
        int page = 0;
        int size = 0;

        Throwable throwable = catchThrowable(() -> controller.getData(JOB_ID, DROP_CHART.getName(), since, until,
            page, size, orderBy, orderMode, QUERY, DropReportType.LAST_ISO));

        assertThat(throwable)
            .isInstanceOf(ConstraintViolationException.class);
        assertThat(((ConstraintViolationException) throwable).getConstraintViolations())
            .hasSize(2);
    }

    @Test
    public void getDropReportWithNonExistingJob() {
        LocalDateTime until = null;
        Throwable throwable = catchThrowable(() -> controller.getData(UNKNOWN_JOB_ID, DROP_TABLE.getName(),
            DEFAULT_SINCE, until, PAGE, SIZE, DEFAULT_ORDER_BY, ASC, QUERY, DropReportType.LAST_ISO));

        assertThat(throwable)
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(RESOURCE_NOT_FOUND.getMessage());
    }

    @Test
    public void getEmptyDropReportWithTimeFilter() {
        LocalDateTime since = LocalDateTime.of(2016, 2, 17, 0, 10, 10);
        LocalDateTime until = null;
        DocumentList<? extends DropReportBean> response = controller.getData(JOB_ID, DROP_TABLE.getName(), since, until,
            PAGE, SIZE, DEFAULT_ORDER_BY, ASC, QUERY, DropReportType.LAST_ISO);

        assertThat(response).isNotNull();
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getMeta()).isNotNull();
        assertThat(response.getMeta().getMeta())
            .contains(entry(TOTAL_ITEMS, 0L), entry(ITEM_PER_PAGE, 20), entry(CURRENT_PAGE, 1));
    }

    @Test
    public void getDefaultDropReportDataForChart() {
        LocalDateTime until = LocalDateTime.of(2016, 2, 6, 12, 15, 3);
        String orderBy = null;
        SortingMode orderMode = null;
        int page = 1;
        int size = 1;

        DocumentList<? extends DropReportBean> response = controller.getData(JOB_ID, DROP_CHART.getName(),
            DEFAULT_SINCE, until, page, size, orderBy, orderMode, QUERY, DropReportType.LAST_ISO);

        assertThat(response)
            .as("Response")
            .isNotNull();
        assertThat(response.getErrors())
            .as("Response errors")
            .isNullOrEmpty();
        assertThat(response.getMeta())
            .as("Response Meta")
            .isNull();
        assertThat(response.getData())
            .as("Response Data")
            .isNotNull();

        List<? extends DropReportBean> result = response.unwrap();
        assertThat(result)
            .as("Drop Reports")
            .hasSize(2)
            .doesNotContainNull()
            .doesNotHaveDuplicates();

        assertThat(result)
            .extracting(DropReportBean::getId)
            .as("Drop Name")
            .contains(DROP_NAME_16_3, DROP_NAME_16_4);

        assertThat(result)
            .extracting(DropReportBean::getIsoLastStartTime)
            .as("ISO last start date")
            .isNotNull();

        DropReportBean firstDrop = result.stream()
            .filter(dropBean -> Objects.equals(dropBean.getId(), DROP_NAME_16_3))
            .findFirst().get();
        DropReportBean lastDrop = result.stream()
            .filter(dropBean -> Objects.equals(dropBean.getId(), DROP_NAME_16_4))
            .findFirst().get();

        assertThat(firstDrop.getIsoLastStartTime())
            .as("ISO last start date")
            .isAfter(DEFAULT_SINCE)
            .isBefore(lastDrop.getIsoLastStartTime());

        assertThat(result)
            .extracting(DropReportBean::getLatestIsoVersion)
            .as("Latest ISO version")
            .contains("1.4.9", "1.2.0");

        assertThat(result)
            .extracting(DropReportBean::getPassRate)
            .as("Pass Rate")
            .contains(Integer.valueOf(77), Integer.valueOf(87));

        assertThat(result)
            .extracting(DropReportBean::getTestCasesCount)
            .as("Test Case count")
            .contains(2);

        assertThat((List<JobLastISODropReportBean>) result)
            .extracting(JobLastISODropReportBean::getTestSuitesCount)
            .as("Test Suite count")
            .contains(1);

        assertThat(result)
            .extracting(DropReportBean::getTestSessionsCount)
            .as("Test Session count")
            .contains(1);

        assertThat(result).as("Asc sort of Drop Report results by Drop name")
            .isSortedAccordingTo((dropBean1, dropBean2) -> {
                DefaultArtifactVersion version1 = new DefaultArtifactVersion(dropBean1.getId());
                DefaultArtifactVersion version2 = new DefaultArtifactVersion(dropBean2.getId());
                return version1.compareTo(version2);
            });
    }
}

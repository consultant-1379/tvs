package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.converters.DateConverter;
import com.ericsson.gic.tms.presentation.dto.ContextBean;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Meta;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Resource;
import com.ericsson.gic.tms.presentation.resources.ContextResource;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.JobStatisticsBean;
import com.google.common.collect.ComparisonChain;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.*;
import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.*;
import static com.google.common.collect.Lists.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JobControllerTest extends AbstractIntegrationTest {

    private static final String UNKNOWN_CONTEXT = "unknown";
    private static final String CONTEXT_ID_1 = "systemId-3";
    private static final String CONTEXT_ID_2 = "systemId-1";
    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String EXECUTION_ID_0 = "session_id_0";
    private static final String EXECUTION_ID_1 = "session_id_1";
    private static final String EXECUTION_ID_2 = "session_id_2";
    private static final String EXECUTION_ID_3 = "session_id_3";
    public static final String DATE = "Date";
    public static final String ISO_VERSION = "Iso Version";
    public static final String DROP = "Drop";

    private static final String ID = "id";
    private static final String NAME = "name";
    public static final String EMPTY_QUERY = null;
    public static final Integer LIMIT = 2;
    public static final String QUERY = "name~string|KGB+N_Job";

    private DateConverter dateConverter = new DateConverter(ZoneId.of("UTC"));

    @Autowired
    private JobController controller;

    @Autowired
    private ContextResource contextResource;

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);
        ContextBean contextBean = new ContextBean();
        contextBean.setId(CONTEXT_ID_1);
        doReturn(new DocumentList<>(newArrayList(new Resource<>(contextBean))))
            .when(contextResource).getChildren(CONTEXT_ID_1);

        ContextBean unknownContextBean = new ContextBean();
        unknownContextBean.setId(UNKNOWN_CONTEXT);
        doReturn(new DocumentList<>(newArrayList(new Resource<>(unknownContextBean))))
            .when(contextResource).getChildren(UNKNOWN_CONTEXT);
    }

    @Test
    public void getJob() {
        JobBean queried = controller.getJob(JOB_ID).unwrap();

        assertThat(queried).isNotNull();
        assertThat(queried.getId()).isEqualTo(JOB_ID);
        assertThat(queried.getContext()).isEqualTo(CONTEXT_ID_2);
    }

    @Test
    public void getJobsFirstPage() {
        DocumentList<JobBean> documentList = controller.getList(CONTEXT_ID_1, 1, 20, ID, DESC, EMPTY_QUERY);

        assertThat(documentList).isNotNull();
        assertThat(documentList.getErrors()).isNullOrEmpty();
        assertThat(documentList.getMeta()).isNotNull();
        assertThat(documentList.getMeta().getMeta())
            .hasSize(4)
            .contains(
                entry(TOTAL_ITEMS, 9L),
                entry(ITEM_PER_PAGE, 20),
                entry(CURRENT_PAGE, 1));

        List<JobBean> jobList = documentList.unwrap();
        assertThat(jobList)
            .as("Jobs response page")
            .hasSize(9)
            .doesNotContainNull();

        assertThat(jobList)
            .extracting(JobBean::getId)
            .as("Job ID")
            .isNotEmpty();

        assertThat(jobList)
            .extracting(JobBean::getContext)
            .as("Job Context ID")
            .contains(CONTEXT_ID_1);

        assertThat(jobList)
            .extracting(JobBean::getName)
            .as("Job Name")
            .isNotEmpty();

        assertThat(jobList)
            .extracting(JobBean::getTestSessions)
            .as("Job Sessions")
            .isNotEmpty();
    }

    @Test
    public void getFilteredJobsPage() {
        DocumentList<JobBean> documentList = controller.getList(CONTEXT_ID_1, 1, 20, ID, DESC, QUERY);

        assertThat(documentList).isNotNull();
        assertThat(documentList.getErrors()).isNullOrEmpty();
        assertThat(documentList.getMeta()).isNotNull();
        assertThat(documentList.getMeta().getMeta())
                .hasSize(4)
                .contains(
                        entry(TOTAL_ITEMS, 2L),
                        entry(ITEM_PER_PAGE, 20),
                        entry(CURRENT_PAGE, 1));

        List<JobBean> jobList = documentList.unwrap();
        assertThat(jobList)
                .as("Jobs response page")
                .hasSize(2)
                .doesNotContainNull();
    }

    @Test
    public void getEmptyJobsFirstPage() {
        DocumentList<JobBean> documentList = controller.getList(UNKNOWN_CONTEXT, 1, 20, ID, DESC, EMPTY_QUERY);

        assertThat(documentList).isNotNull();
        assertThat(documentList.getErrors()).isNullOrEmpty();
        assertThat(documentList.getData()).isNullOrEmpty();
        assertThat(documentList.getMeta()).isNotNull();
        assertThat(documentList.getMeta().getMeta())
            .contains(entry(TOTAL_ITEMS, 0L), entry(ITEM_PER_PAGE, 20), entry(CURRENT_PAGE, 1));
    }

    @Test
    public void getJobSessionFirstPageSortedByNameDesc() {
        List<JobBean> jobSessionList = controller.getList(CONTEXT_ID_1, 1, 20, NAME, DESC, EMPTY_QUERY).unwrap();
        assertThat(jobSessionList).as("Desc sort of Job Session collection")
            .isSortedAccordingTo(new JobNameComparator(DESC));
    }

    @Test
    public void getJobSessionFirstPageSortedByNameAsc() {
        List<JobBean> jobSessionList = controller.getList(CONTEXT_ID_1, 1, 20, NAME, ASC, EMPTY_QUERY).unwrap();
        assertThat(jobSessionList).as("Desc sort of Job Session collection")
                .isSortedAccordingTo(new JobNameComparator(ASC));
    }

    @Test
    public void getChildrenInMeta() {
        Document<JobBean> response = controller.getJob(JOB_ID);

        @SuppressWarnings("unchecked")
        List<String> children = (List<String>) response.getMeta().get(Meta.CHILDREN);
        assertThat(children)
            .containsOnly(EXECUTION_ID_0, EXECUTION_ID_1, EXECUTION_ID_2, EXECUTION_ID_3);
    }

    @Test
    public void testSuiteAggregation() {
        List<JobStatisticsBean> statistics =
                controller.getStatistics("d490b7d8e4b03ebcdc2b1234", EMPTY_QUERY, null, DATE).unwrap();

        assertThat(statistics)
            .as("Incorrect number of statistics objects")
            .hasSize(4)
            .doesNotContainNull();

        JobStatisticsBean expected = new JobStatisticsBean();
        expected.setTestSessionId("5644b7d8e4b03ebcdc2b0fc4");
        expected.setTotal(2);
        expected.setPassed(1);
        expected.setCancelled(1);
        expected.setFailed(0);
        expected.setBroken(0);
        expected.setTime(toDate("2015-12-11T14:36:57.500"));
        expected.setIsoVersion("1.18.40");
        expected.setIsoVersionPadded("000100180040");
        expected.setDropName("1.2");

        assertThat(statistics.get(0))
            .as("First statistic is not equal to the expected result")
            .isEqualToComparingFieldByField(expected);

        assertThat(statistics.get(1).getTestSessionId())
            .as("Second statistic has an unexpected id")
            .isEqualTo("0644b7d8e4b03ebcdc2b0fc0");
    }

    @Test
    public void testSuiteAggregationOrderedByIso() {
        List<JobStatisticsBean> statistics =
                controller.getStatistics("d490b7d8e4b03ebcdc2b1234", EMPTY_QUERY, null, ISO_VERSION).unwrap();

        assertThat(statistics)
                .as("Incorrect number of statistics objects")
                .hasSize(4)
                .doesNotContainNull();

        JobStatisticsBean expected = new JobStatisticsBean();
        expected.setTestSessionId("0644b7d8e4b03ebcdc2b0fc0");
        expected.setTotal(0);
        expected.setPassed(0);
        expected.setCancelled(0);
        expected.setFailed(0);
        expected.setBroken(0);
        expected.setTime(toDate("2015-12-11T14:36:58.000"));
        expected.setIsoVersion("1.18.4");
        expected.setIsoVersionPadded("000100180004");
        expected.setDropName("1.2");

        assertThat(statistics.get(0))
                .as("First statistic is not equal to the expected result")
                .isEqualToComparingFieldByField(expected);

        assertThat(statistics.get(0).getIsoVersion()).isEqualTo("1.18.4");
        assertThat(statistics.get(1).getIsoVersion()).isEqualTo("1.18.40");
        assertThat(statistics.get(2).getIsoVersion()).isEqualTo("1.18.41");
        assertThat(statistics.get(3).getIsoVersion()).isEqualTo("1.18.42");
    }

    @Test
    public void testSuiteAggregationOrderedByDrop() {
        List<JobStatisticsBean> statistics =
                controller.getStatistics("d490b7d8e4b03ebcdc2b1234", EMPTY_QUERY, null, DROP).unwrap();

        assertThat(statistics)
                .as("Incorrect number of statistics objects")
                .hasSize(4)
                .doesNotContainNull();

        JobStatisticsBean expected = new JobStatisticsBean();
        expected.setTestSessionId("5644b7d8e4b03ebcdc2b0fc4");
        expected.setTotal(2);
        expected.setPassed(1);
        expected.setCancelled(1);
        expected.setFailed(0);
        expected.setBroken(0);
        expected.setTime(toDate("2015-12-11T14:36:57.500"));
        expected.setIsoVersion("1.18.40");
        expected.setIsoVersionPadded("000100180040");
        expected.setDropName("1.2");

        assertThat(statistics.get(0))
                .as("First statistic is not equal to the expected result")
                .isEqualToComparingFieldByField(expected);

        assertThat(statistics.get(0).getDropName()).isEqualTo("1.2");
        assertThat(statistics.get(1).getDropName()).isEqualTo("1.2");
        assertThat(statistics.get(2).getDropName()).isEqualTo("1.3");
        assertThat(statistics.get(3).getDropName()).isEqualTo("1.4");
    }

    @Test
    public void testSuiteAggregationDefaultParam() {
        List<JobStatisticsBean> statistics =
            controller.getStatistics("d490b7d8e4b03ebcdc2b1234", EMPTY_QUERY, null, null).unwrap();

        assertThat(statistics)
            .as("Incorrect number of statistics objects")
            .hasSize(4)
            .doesNotContainNull();

        JobStatisticsBean expected = new JobStatisticsBean();
        expected.setTestSessionId("5644b7d8e4b03ebcdc2b0fc4");
        expected.setTotal(2);
        expected.setPassed(1);
        expected.setCancelled(1);
        expected.setFailed(0);
        expected.setBroken(0);
        expected.setTime(toDate("2015-12-11T14:36:57.500"));
        expected.setIsoVersion("1.18.40");
        expected.setIsoVersionPadded("000100180040");
        expected.setDropName("1.2");

        assertThat(statistics.get(0))
            .as("First statistic is not equal to the expected result")
            .isEqualToComparingFieldByField(expected);

        assertThat(statistics.get(1).getTestSessionId())
            .as("Second statistic has an unexpected id")
            .isEqualTo("0644b7d8e4b03ebcdc2b0fc0");
    }

    @Test
    public void testSuiteAggregationWithLimit() {

        List<JobStatisticsBean> statistics =
                controller.getStatistics("d490b7d8e4b03ebcdc2b1234", EMPTY_QUERY, LIMIT, DATE).unwrap();

        assertThat(statistics).hasSize(LIMIT);
        assertThat(statistics).extracting("testSessionId")
            .containsExactly("5644b7d9e4b03ebcdc2b0fcd", "5645a0f2e4b03ebcdc2b0fce");

    }

    @Test
    public void aggregateJob() {
        JobBean expected = new JobBean();
        expected.setId(JOB_ID);
        expected.setName("X_SCHEDULER_1");
        expected.setContext(CONTEXT_ID_2);
        expected.setTestSessions(null);
        expected.setTestSessionCount(4);
        expected.setLastTestSessionTestSuiteCount(1);
        expected.setLastTestSessionTestCaseCount(3);
        expected.setLastExecutionDate(toDate("2015-12-11T16:00:01"));
        expected.setLastTestSessionDuration(2216000L);
        expected.setAvgTestSessionDuration(3832750L);

        JobBean jobBean = controller.aggregateJob(JOB_ID).unwrap();

        assertThat(jobBean)
            .isEqualToIgnoringGivenFields(expected, "modifiedDate", "lastExecutionDate");
    }

    private Date toDate(String date) {
        return dateConverter.fromString(date);
    }

    private class JobNameComparator implements Comparator<JobBean> {

        private SortingMode sortingMode;

        JobNameComparator(SortingMode sortingMode) {
            this.sortingMode = sortingMode;
        }

        public int compare(JobBean jobBean1, JobBean jobBean2) {
            switch (sortingMode) {
                case ASC:
                    return ComparisonChain.start()
                        .compare(jobBean1.getName(), jobBean2.getName())
                        .result();
                default:
                    return ComparisonChain.start()
                        .compare(jobBean2.getName(), jobBean1.getName())
                        .result();
            }
        }
    }
}

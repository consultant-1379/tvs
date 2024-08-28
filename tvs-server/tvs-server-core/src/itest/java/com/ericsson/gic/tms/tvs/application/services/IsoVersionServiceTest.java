package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.presentation.dto.ContextBean;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Resource;
import com.ericsson.gic.tms.presentation.resources.ContextResource;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

public class IsoVersionServiceTest extends AbstractIntegrationTest {

    private static final String CONTEXT_ID = "68290196-9874-4b21-ba0a-3472ceb0c5a1";

    @Autowired
    private IsoVersionService isoVersionService;

    @Autowired
    private ContextResource contextResource;

    @Autowired
    private JobService jobService;

    private Date startOfTest;

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
    public void findLastIsoVersionsTop3() {
        prepareData();

        long maxSize = 3;
        List<String> isoVersions = isoVersionService.findLastIsoVersionsTopN(newArrayList(CONTEXT_ID), maxSize);
        assertThat(isoVersions)
            .as("ISO versions")
            .containsExactly("1.1.17", "1.1.11", "1.1.10");
    }

    @Test
    public void findLastIsoVersionsTop3WithEmptyContext() {
        long maxSize = 3;
        Throwable throwable = catchThrowable(() -> isoVersionService.findLastIsoVersionsTopN(null, maxSize));
        assertThat(throwable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("At least 1 context must be specified");
    }

    @Test
    public void findIsoVersionsInContextsByFullIsoRange() {
        prepareData();
        String fromIsoVersion = "1.1.1";
        String toIsoVersion = "1.1.11";

        List<String> isoVersions = isoVersionService.findIsoVersionsInContexts(newArrayList(CONTEXT_ID),
            fromIsoVersion, toIsoVersion);
        assertThat(isoVersions)
            .as("ISO versions")
            .contains("1.1.1", "1.1.2", "1.1.3", "1.1.10", "1.1.11");
    }

    @Test
    public void findIsoVersionsInContextsByStartIso() {
        prepareData();

        String fromIsoVersion = "1.1.11";
        String toIsoVersion = null;

        List<String> isoVersions = isoVersionService.findIsoVersionsInContexts(newArrayList(CONTEXT_ID),
            fromIsoVersion, toIsoVersion);
        assertThat(isoVersions)
            .as("ISO versions")
            .contains("1.1.11", "1.1.17");
    }

    @Test
    public void findIsoVersionsInContextsByEndIso() {
        prepareData();

        String fromIsoVersion = null;
        String toIsoVersion = "1.1.3";

        List<String> isoVersions = isoVersionService.findIsoVersionsInContexts(newArrayList(CONTEXT_ID),
            fromIsoVersion, toIsoVersion);
        assertThat(isoVersions)
            .as("ISO versions")
            .contains("1.1.1", "1.1.2", "1.1.3");
    }

    @Test
    public void findIsoVersionsInContextsByEmptyIsoRange() {
        prepareData();

        String fromIsoVersion = null;
        String toIsoVersion = null;

        List<String> isoVersions = isoVersionService.findIsoVersionsInContexts(newArrayList(CONTEXT_ID),
            fromIsoVersion, toIsoVersion);
        assertThat(isoVersions)
            .as("ISO versions")
            .contains("1.1.1", "1.1.2", "1.1.3", "1.1.10", "1.1.11", "1.1.17");
    }

    private void prepareData() {
        JobBean job = job(
            testSessionWithAdditionalData("1.1.1",
                testSuiteResult(
                    testCaseResultWithAdditionalData(PASSED, "1.1.1", "Minor", newArrayList("PMIC"),
                        newArrayList("SSO Utilities")),
                    testCaseResultWithAdditionalData(FAILED, "1.1.1", "Normal", newArrayList("OpenDJ"),
                        newArrayList("SSO Utilities"))
                )
            ),
            testSessionWithAdditionalData("1.1.11",
                testSuiteResult(
                    testCaseResultWithAdditionalData(FAILED, "1.1.11", "Minor", newArrayList("PMIC", "OpenDJ"),
                        newArrayList("SSO Utilities")),
                    testCaseResultWithAdditionalData(PASSED, "1.1.11", "Blocker", newArrayList("PMIC", "NAM TMS"),
                        newArrayList("License Management"))
                ),
                testSuiteResult(
                    testCaseResultWithAdditionalData(FAILED, "1.1.11", "Blocker", newArrayList("PMIC", "OpenDJ"),
                        newArrayList("License Management"))
                )
            ),
            testSessionWithAdditionalData("1.1.2",
                testSuiteResult(
                    testCaseResultWithAdditionalData(PASSED, "1.1.2", "Minor", newArrayList("NAM TMS"),
                        newArrayList("License Management"))
                )
            ),
            testSessionWithAdditionalData("1.1.3",
                testSuiteResult(
                    testCaseResultWithAdditionalData(PASSED, "1.1.3", "Normal", newArrayList("NAM TMS"),
                        newArrayList("License Management"))
                )
            ),
            testSessionWithAdditionalData("1.1.17",
                testSuiteResult(
                    testCaseResultWithAdditionalData(PASSED, "1.1.17", "Normal", newArrayList("NAM TMS"),
                        newArrayList("TAF TE"))
                )
            ),
            testSessionWithAdditionalData("1.1.10",
                testSuiteResult(
                    testCaseResultWithAdditionalData(FAILED, "1.1.10", "Normal", newArrayList("NAM TMS"),
                        newArrayList("TAF TE"))
                )
            )
        );

        jobService.updateJob(CONTEXT_ID, job.getName(), job);
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

    private TestSessionBean testSessionWithAdditionalData(String iso, TestSuiteResultBean... testSuiteResults) {
        TestSessionBean testSessionBean = testSession(uniqueString(), testSuiteResults);
        testSessionBean.addAdditionalFields(ISO_VERSION, iso);

        return testSessionBean;
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

}

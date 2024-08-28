package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Created by eniakel on 16/11/2016.
 */
public class TestCaseResultControllerTest extends AbstractIntegrationTest {

    private static final int PAGE = 1;
    private static final int SIZE = 20;
    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String EXECUTION_ID = "session_id_1";
    private static final String SUITE_NAME = "testsuite_id_1";
    private static final String ISO_VERSION_PADDED = "ISO_VERSION_PADDED";
    private static final String ISO_VERSION = "ISO_VERSION";

    @Autowired
    private TestCaseResultController controller;

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);
    }

    @Test
    public void getTestCaseResultsSortedByISOVersion() {
        List<TestCaseResultBean> response = controller.getList(
                JOB_ID, EXECUTION_ID, SUITE_NAME, PAGE, SIZE, ISO_VERSION_PADDED, DESC, null).unwrap();

        assertThat(response)
                .as("Test Case Results Response")
                .hasSize(6)
                .doesNotContainNull()
                .doesNotHaveDuplicates();

        assertThat(response.get(0).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.42");
        assertThat(response.get(1).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.42");
        assertThat(response.get(2).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.41");
        assertThat(response.get(3).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.40");
        assertThat(response.get(4).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.39");
        assertThat(response.get(5).getAdditionalFields().get(ISO_VERSION)).isEqualTo("1.18.4");
    }
}
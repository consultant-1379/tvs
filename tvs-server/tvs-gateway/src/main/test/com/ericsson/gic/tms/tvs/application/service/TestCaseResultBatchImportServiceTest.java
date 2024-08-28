package com.ericsson.gic.tms.tvs.application.service;

import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.ericsson.gic.tms.tvs.application.service.TestCaseResultBatchImportService.*;
import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.google.common.collect.Lists.*;
import static org.assertj.core.api.Assertions.*;

public class TestCaseResultBatchImportServiceTest {

    private TestCaseResultBatchImportService service;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        service = new TestCaseResultBatchImportService("");
    }

    @Test
    public void testMergeTestCaseResults() {
        List<TestCaseResultBean> list1 = newArrayList(
            testCaseResult("test1", NOT_STARTED_CODE),
            testCaseResult("test2", NOT_STARTED_CODE),
            testCaseResult("test3", NOT_STARTED_CODE)
            );

        List<TestCaseResultBean> list2 = newArrayList(
            testCaseResult("test1", PASSED),
            testCaseResult("test2", NOT_STARTED_CODE),
            testCaseResult("test3", PASSED),
            testCaseResult("test4", NOT_STARTED_CODE)
            );

        List<TestCaseResultBean> result = service.mergeTestCaseResults(list1, list2);

        assertThat(result)
            .hasSize(4)
            .extracting(TestCaseResultBean::getId, TestCaseResultBean::getResultCode)
            .contains(
                tuple("test1", PASSED),
                tuple("test2", NOT_STARTED_CODE),
                tuple("test3", PASSED),
                tuple("test4", NOT_STARTED_CODE));
    }
}

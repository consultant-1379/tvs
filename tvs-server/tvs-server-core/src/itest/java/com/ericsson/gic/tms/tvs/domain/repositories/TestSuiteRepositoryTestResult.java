package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestSuiteRepositoryTestResult extends AbstractIntegrationTest {

    private static final String SESSION_ID_1 = "5644b7d8e4b03ebcdc2b0fc4";
    private static final String SESSION_ID_2 = "5644b7d9e4b03ebcdc2b0fcd";

    private static final String SUITE_NAME_1 = "testsuite_id_1";
    private static final String SUITE_NAME_2 = "testsuite_id_2";

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Test
    public void findByTestSessionIdPageable() {
        Page<TestSuiteResult> result =
            testSuiteResultRepository.findByTestSessionId(SESSION_ID_1, mock(Pageable.class), new Query());

        assertThat(result.getTotalElements()).isEqualTo(1);

        TestSuiteResult resultSuite = result.getContent().get(0);
        assertThat(resultSuite.getName()).isEqualTo(SUITE_NAME_1);
    }

    @Test
    public void testFindByTestSessionId() {
        List<TestSuiteResult> result = testSuiteResultRepository.findByTestSessionId(SESSION_ID_2);
        assertThat(result.size()).isEqualTo(1);

        TestSuiteResult resultSuite = result.get(0);
        assertThat(resultSuite.getName()).isEqualTo(SUITE_NAME_2);
    }

    @Test
    public void testFindByTestSessionIdAndSystemId() {
        TestSuiteResult resultSuite =
            testSuiteResultRepository.findByTestSessionIdAndName(SESSION_ID_2, SUITE_NAME_2);
        assertThat(resultSuite.getName()).isEqualTo(SUITE_NAME_2);
    }

}

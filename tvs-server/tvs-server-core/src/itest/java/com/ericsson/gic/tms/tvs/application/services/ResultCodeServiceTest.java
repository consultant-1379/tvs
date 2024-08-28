package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ericsson.gic.tms.tvs.application.services.ResultCodeService.DEFAULT_INTERNAL_CODE;
import static com.ericsson.gic.tms.tvs.presentation.constant.TestCaseResultSource.TAF_EIFFEL;
import static com.ericsson.gic.tms.tvs.presentation.constant.TestCaseResultSource.TAF_TMS;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.CANCELLED;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.PASSED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         14/09/2016
 */
public class ResultCodeServiceTest extends AbstractIntegrationTest {

    @Autowired
    private ResultCodeService service;

    @Test
    public void testTmsExternalCode() {
        String code = service.findByExternalCode(TAF_TMS.name(), "PASS").getInternalCode();
        assertThat(code).isEqualTo(PASSED.name());
    }

    @Test
    public void testEiffelExternalCode() {
        String code = service.findByExternalCode(TAF_EIFFEL.name(), "SUCCESS").getInternalCode();
        assertThat(code).isEqualTo(PASSED.name());
    }

    @Test
    public void testAnySourceExternalCode() {
        String code = service.findByExternalCode("BLOCKED").getInternalCode();
        assertThat(code).isEqualTo(CANCELLED.name());
    }

    @Test
    public void testUnknownCode() {
        String code = service.findByExternalCode(TAF_TMS.name(), "UNKNOWN").getInternalCode();
        assertThat(code).isEqualTo(DEFAULT_INTERNAL_CODE);
    }

    @Test
    public void testUnknownSource() {
        String code = service.findByExternalCode("UNKNOWN", "SUCCESS").getInternalCode();
        assertThat(code).isEqualTo(DEFAULT_INTERNAL_CODE);
    }

    @Test(expected = NullPointerException.class)
    public void testNullCode() {
        service.findByExternalCode(null);
    }

    @Test
    public void testInternalCode() {
        String code = service.findByExternalCode("PASSED").getInternalCode();
        assertThat(code).isEqualTo(PASSED.name());
    }

}

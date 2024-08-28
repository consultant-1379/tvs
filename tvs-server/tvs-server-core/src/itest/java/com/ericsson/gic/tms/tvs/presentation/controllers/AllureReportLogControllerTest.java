package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.AllureReportLogBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.*;

public class AllureReportLogControllerTest extends AbstractIntegrationTest {

    private static final String EXECUTION_ID = "job_session_id_1";
    private static final String INVALID_EXECUTION_ID = "invalid_execution_id";

    @Autowired
    private AllureReportLogController controller;

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);
    }

    @Test
    public void addAllureReportLog() {
        AllureReportLogBean logBean = new AllureReportLogBean();
        logBean.setJobExecutionId(EXECUTION_ID);
        assertAllureReportResponse(controller.addLog(logBean));
    }

    @Test
    public void addAndFindAllureReportLog() {
        AllureReportLogBean logBean = new AllureReportLogBean();
        logBean.setJobExecutionId(EXECUTION_ID);
        controller.addLog(logBean);
        assertAllureReportResponse(controller.find(EXECUTION_ID));
    }

    @Test
    public void addDuplicateAndFindSingleAllureReportLog() {
        AllureReportLogBean logBean = new AllureReportLogBean();
        logBean.setJobExecutionId(EXECUTION_ID);
        controller.addLog(logBean);
        controller.addLog(logBean);

        // no expected Duplicate errors
        assertAllureReportResponse(controller.find(EXECUTION_ID));
    }

    @Test
    public void addAllureReportLogWithEmptyJobExecutionId() {
        AllureReportLogBean logBean = new AllureReportLogBean();
        logBean.setJobExecutionId(null);
        assertThatThrownBy(() -> controller.addLog(logBean))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void findAllureReportLogByInvalidJobExecutionId() {
        assertThatThrownBy(() -> controller.find(INVALID_EXECUTION_ID))
            .isInstanceOf(NotFoundException.class);
    }

    private static void assertAllureReportResponse(Document<AllureReportLogBean> response) {
        assertThat(response)
            .as("response")
            .isNotNull();
        assertThat(response.getErrors())
            .as("response errors")
            .isNullOrEmpty();
        assertThat(response.getData())
            .as("response data")
            .isNotNull();
        assertThat(response.getMeta())
            .as("response meta")
            .isNull();

        AllureReportLogBean allureReportLog = response.unwrap();

        assertThat(allureReportLog)
            .as("Allure Report Log")
            .isNotNull();
        assertThat(allureReportLog.getId())
            .as("Allure Report Log ID")
            .isNotNull();
        assertThat(allureReportLog.getJobExecutionId())
            .as("Allure Report job execution ID")
            .isEqualTo(EXECUTION_ID);
    }
}

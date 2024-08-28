package com.ericsson.gic.tms.tvs.application.sample;

import com.ericsson.gic.tms.tvs.application.service.TestCaseResultBatchImportService;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.collect.Lists.*;

public final class SampleBatchImport {

    private SampleBatchImport() {}

    private static final Logger LOGGER = Logger.getLogger(SampleBatchImport.class.getName());

    public static void main(String[] args) throws NoSuchAlgorithmException {
        if (args.length == 0) {
            LOGGER.info("No params provided. Exiting");
            return;
        }
        String tvsUrl = args[0];

        TestCaseResultBatchImportService service = new TestCaseResultBatchImportService(tvsUrl);

        TestCaseResultBean testCaseResult1 = new TestCaseResultBean();
        testCaseResult1.setId("DURACI-3155_Func_1");
        testCaseResult1.setName("Get user assignment inbox");
        testCaseResult1.setResultCode("PASSED");
        testCaseResult1.setTime(createExecutionTime(new Date(200000), new Date(400000)));

        TestCaseResultBean testCaseResult2 = new TestCaseResultBean();
        testCaseResult2.setId("DURACI-2269_Func_3");
        testCaseResult2.setName("Execute all Test Cases export");
        testCaseResult2.setResultCode("NOT_STARTED");
        testCaseResult2.setTime(createExecutionTime(new Date(550000), new Date(580000)));

        TestCaseResultBean testCaseResult3 = new TestCaseResultBean();
        testCaseResult3.setId("DURACI-2526_Func_2");
        testCaseResult3.setName("Check that selected project is persisted");
        testCaseResult3.setResultCode("PASSED");
        testCaseResult3.setTime(createExecutionTime(new Date(600000), new Date(640000)));

        List<TestCaseResultBean> results = newArrayList(testCaseResult1, testCaseResult2, testCaseResult3);

        String testActivity = "Test Campaign 1";
        String dropName = "16.2";
        String isoVersion = "1.8.20";
        service.importTestCaseResults(testActivity, dropName, isoVersion, results);
    }

    private static ExecutionTimeBean createExecutionTime(Date start, Date stopDate) {
        ExecutionTimeBean executionTime = new ExecutionTimeBean();
        executionTime.setStartDate(start);
        executionTime.setStopDate(stopDate);
        return executionTime;
    }
}

package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.application.util.VersionConverter;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSuiteResultRepository;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestCaseMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestCaseResultReportMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.*;
import static java.util.stream.Collectors.*;

@Service
public class TestCaseResultService {

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TestCaseResultReportMapper testCaseResultReportMapper;

    public List<TestCaseResultReport> findTestCaseResults(String jobId, String jobExecutionId) {
        verifyFound(testSessionRepository.findByJobIdAndExecutionId(jobId, jobExecutionId));
        List<TestCaseResult> list = testCaseResultRepository.findByJobExecutionId(jobExecutionId);
        return testCaseResultReportMapper.mapAsList(list);
    }

    public List<TestCaseResultReport> findTestCaseResults(String jobId, String jobExecutionId, String testSuiteName) {
        verifyFound(testSessionRepository.findByJobIdAndExecutionId(jobId, jobExecutionId));
        List<TestCaseResult> list = testCaseResultRepository.findByJobExecutionAndSuite(jobExecutionId, testSuiteName);
        return testCaseResultReportMapper.mapAsList(list);
    }

    public Page<TestCaseResultBean> getPaginatedTestCases(String jobId, String executionId,
                                                          String testSuiteName, Pageable pageRequest, Query query) {
        String suiteResultId = getTestSuiteResultId(jobId, executionId, testSuiteName);
        Page<TestCaseResult> page = testCaseResultRepository.findByTestSuiteResultId(suiteResultId, pageRequest, query);

        return testCaseMapper.mapAsPage(page, pageRequest);
    }

    public TestCaseResultBean getTestCaseResult(String jobId,
                                                String executionId,
                                                String testSuiteName,
                                                String testCaseId) {
        String testSuiteResultId = getTestSuiteResultId(jobId, executionId, testSuiteName);
        TestCaseResult testCaseResult = verifyFound(
            testCaseResultRepository.findByTestCaseIdAndAndTestSuiteResultId(testCaseId, testSuiteResultId));
        return testCaseMapper.toDto(testCaseResult);
    }

    private String getTestSuiteResultId(String jobId, String executionId, String testSuiteName) {
        String sessionId = verifyFound(
            testSessionRepository.findByExecutionIdAndJobId(executionId, jobId)).getId();
        return verifyFound(
            testSuiteResultRepository.findByTestSessionIdAndName(sessionId, testSuiteName)).getId();
    }

    public TestCaseResultBean updateTestCaseResult(String executionId, TestCaseResultBean bean) {
        TestCaseResult testCaseResult = verifyFound(testCaseResultRepository.findByTestCaseId(executionId));
        testCaseMapper.copyFields(bean, testCaseResult);
        testCaseResult = testCaseResultRepository.save(testCaseResult);

        return testCaseMapper.toDto(testCaseResult);
    }

    public TestCaseResultBean updateTestCaseResult(ResultPath resultPath, String testCaseId, TestCaseResultBean bean) {
        bean.setId(testCaseId);
        return updateTestCaseResult(resultPath, bean);
    }

    protected List<TestCaseResultBean> updateTestCaseResults(ResultPath resultPath, List<TestCaseResultBean> beans) {
        if (beans == null) {
            return Collections.emptyList();
        }
        return beans.stream()
            .map(testCaseBean -> updateTestCaseResult(resultPath, testCaseBean))
            .collect(toList());
    }

    private TestCaseResultBean updateTestCaseResult(ResultPath parentResultPath, TestCaseResultBean bean) {
        String testCaseId = bean.getId();
        String testSuiteResultId = parentResultPath.getTestSuiteResultId();
        TestCaseResult testCaseResult =
            testCaseResultRepository.findByTestCaseIdAndAndTestSuiteResultId(testCaseId, testSuiteResultId);

        if (testCaseResult == null) {
            testCaseResult = new TestCaseResult();
        }
        testCaseMapper.copyFields(bean, testCaseResult);

        testCaseResult.setTestCaseId(testCaseId);
        testCaseResult.setTestSuiteResultId(testSuiteResultId);
        testCaseResult.setTestSuiteName(parentResultPath.getTestSuiteName());
        testCaseResult.setExecutionId(parentResultPath.getExecutionId());
        testCaseResult.setJobId(parentResultPath.getJobId());
        testCaseResult.setContextId(parentResultPath.getContextId());

        String isoVersion = (String) testCaseResult.getAdditionalField("ISO_VERSION");
        String paddedIsoVersion = VersionConverter.getPaddedVersion(isoVersion);
        if (paddedIsoVersion != null) {
            testCaseResult.addAdditionalFields("ISO_VERSION_PADDED", paddedIsoVersion);
        }

        testCaseResult = testCaseResultRepository.save(testCaseResult);
        return testCaseMapper.toDto(testCaseResult);
    }

    public List<String> getTestCaseIds(String testSuiteId) {
        return testCaseResultRepository.findByTestSuiteResultId(testSuiteId)
            .stream()
            .map(TestCaseResult::getTestCaseId)
            .collect(toList());
    }
}

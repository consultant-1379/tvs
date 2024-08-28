package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.application.util.VersionConverter;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Statistics;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSuiteResultRepository;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestSuiteMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode;
import com.google.common.collect.Iterables;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.verifyFound;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.TEST_SUITE_RESULT_STATISTICS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class TestSuiteResultService {

    private static final Statistics EMPTY_STATISTICS = new Statistics();

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestCaseResultService testCaseResultService;

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private TestSuiteMapper testSuiteMapper;

    @Autowired
    private MongoQueryService queryService;

    @Autowired
    private Jongo jongo;

    public Page<TestSuiteResultBean> getPaginatedTestSuites(String jobId, String executionId, Pageable pageRequest,
                                                           Query query) {
        String sessionId = verifyFound(testSessionRepository.findByExecutionIdAndJobId(executionId, jobId)).getId();

        Page<TestSuiteResult> testSuites = testSuiteResultRepository.findByTestSessionId(sessionId, pageRequest, query);
        return testSuiteMapper.mapAsPage(testSuites, pageRequest);
    }

    public TestSuiteResultBean getTestSuiteResult(String jobId, String executionId, String testSuiteName) {
        TestSuiteResult suite =
            verifyFound(getTestSuiteResultEntity(jobId, executionId, testSuiteName));
        return testSuiteMapper.toDto(suite);
    }

    public List<TestSuiteResult> getAllTestSuites(String testSessionId) {
        return testSuiteResultRepository.findByTestSessionId(testSessionId);
    }

    protected List<TestSuiteResultBean> updateTestSuites(ResultPath resultPath,
                                                         List<TestSuiteResultBean> testSuiteResultBeans) {
        if (testSuiteResultBeans == null) {
            return emptyList();
        }
        return testSuiteResultBeans.stream()
            .map(testSuiteBean -> updateTestSuiteResult(resultPath, testSuiteBean.getId(), testSuiteBean))
            .collect(toList());
    }

    public TestSuiteResultBean updateTestSuiteResult(ResultPath parentResultPath,
                                                     String suiteName,
                                                     TestSuiteResultBean bean) {
        TestSuiteResult testSuiteResult =
            testSuiteResultRepository.findByTestSessionIdAndName(parentResultPath.getTestSessionId(), suiteName);

        if (testSuiteResult == null) {
            testSuiteResult = new TestSuiteResult();
        }

        testSuiteMapper.copyFields(bean, testSuiteResult);
        testSuiteResult.setName(suiteName);
        testSuiteResult.setTestSessionId(parentResultPath.getTestSessionId());
        if (testSuiteResult.getAdditionalField("ISO_VERSION") != null) {
            testSuiteResult.addAdditionalFields("ISO_VERSION_PADDED",
                VersionConverter.getPaddedVersion(testSuiteResult.getAdditionalField("ISO_VERSION").toString()));
        }

        testSuiteResult = testSuiteResultRepository.save(testSuiteResult);

        ResultPath resultPath = parentResultPath.withTestSuiteResult(suiteName, testSuiteResult.getId());
        TestSuiteResultBean testSuiteResultBean = testSuiteMapper.toDto(testSuiteResult);
        List<TestCaseResultBean> testCaseResultBeans =
            testCaseResultService.updateTestCaseResults(resultPath, bean.getTestCaseResults());
        testSuiteResultBean.setTestCaseResults(testCaseResultBeans);
        return testSuiteResultBean;
    }

    public List<String> getSuiteIds(String testSessionId) {
        return testSuiteResultRepository.findByTestSessionId(testSessionId)
            .stream()
            .map(TestSuiteResult::getName)
            .collect(toList());
    }

    public List<String> getTestSuiteChildrenIds(String jobId, String executionId, String testSuiteName) {
        TestSuiteResult suite = getTestSuiteResultEntity(jobId, executionId, testSuiteName);

        return testCaseResultService.getTestCaseIds(suite.getId());
    }

    public TestSuiteResult getTestSuiteResultEntity(String jobId, String executionId, String testSuiteName) {
        String sessionId = verifyFound(testSessionRepository.findByExecutionIdAndJobId(executionId, jobId)).getId();
        return testSuiteResultRepository.findByTestSessionIdAndName(sessionId, testSuiteName);
    }

    public TestSuiteResultBean aggregateTestSuite(String jobId, String executionId, String suiteName,
                                                  TraversalMode mode) {
        TestSuiteResultBean suiteResult = aggregateTestSuite(jobId, executionId, suiteName);

        if (mode == TraversalMode.ASCENDING || mode == TraversalMode.WIDE) {
            testSessionService.aggregateTestSession(jobId, executionId, TraversalMode.ASCENDING);
        }
        return suiteResult;
    }

    public TestSuiteResultBean aggregateTestSuite(String jobId, String executionId, String suiteName) {
        TestSuiteResult suiteResult = verifyFound(getTestSuiteResultEntity(jobId, executionId, suiteName));

        Statistics statistics = Optional.ofNullable(calculateStatistics(suiteResult.getId())).orElse(EMPTY_STATISTICS);
        int passRate;
        if (statistics.getTotal() == 0) {
            passRate = 0;
        } else {
            passRate = statistics.getPassed() * 100 / statistics.getTotal();
        }
        suiteResult.setPassRate(passRate);
        suiteResult.setStatistics(statistics);

        return testSuiteMapper.toDto(testSuiteResultRepository.save(suiteResult));
    }

    private Statistics calculateStatistics(String testSuiteResultId) {
        return Iterables.getFirst(jongo.getCollection("testCaseResult")
            .aggregate("{$match: { testSuiteResultId: # }}", testSuiteResultId)
            .and(queryService.getQuery(TEST_SUITE_RESULT_STATISTICS))
            .as(Statistics.class), null);
    }

}

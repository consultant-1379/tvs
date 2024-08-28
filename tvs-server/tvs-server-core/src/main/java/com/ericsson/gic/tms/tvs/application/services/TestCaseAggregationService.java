package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResultAggregate;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResultId;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSuiteResultRepository;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestCaseIdMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestVerdictMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseIdBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestVerdictBean;
import com.google.common.collect.Iterables;
import org.jongo.Aggregate;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.*;

@Service
public class TestCaseAggregationService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestSuiteResultRepository testSuiteResultRepository;

    @Autowired
    private TestVerdictMapper mapper;

    @Autowired
    private TestCaseIdMapper idMapper;

    @Autowired
    private Jongo jongo;

    @Autowired
    private MongoQueryService mongoQueryService;

    public List<TestCaseIdBean> getTestCaseResultIds(LocalDateTime dateFrom) {
        String testCaseResultIds = mongoQueryService.getQuery(TEST_CASE_RESULT_IDS);
        String projectTestCaseId = mongoQueryService.getQuery(PROJECT_TEST_CASE_ID);
        String matchGtDate = mongoQueryService.getQuery(MATCH_GT_DATE);

        MongoCollection testCaseResults = jongo.getCollection(TEST_CASE_RESULT.getName());
        Aggregate aggregate;

        if (dateFrom != null) {
            aggregate = testCaseResults
                .aggregate(matchGtDate, DateUtils.toDate(dateFrom))
                .and(testCaseResultIds)
                .and(projectTestCaseId);
        } else {
            aggregate = testCaseResults
                .aggregate(testCaseResultIds)
                .and(projectTestCaseId);
        }
        return idMapper.mapAsList(aggregate.as(TestCaseResultId.class));
    }

    public TestVerdictBean getTestCaseResultStatistics(String testCaseId) {
        String testCaseResultStatistics = mongoQueryService.getQuery(TEST_CASE_RESULT_STATISTICS);
        String matchName = mongoQueryService.getQuery(MATCH_NAME);

        TestCaseResultAggregate testCase = verifyFound(
            Iterables.getFirst(jongo.getCollection(TEST_CASE_RESULT.getName())
                .aggregate(matchName, testCaseId)
                .and(testCaseResultStatistics)
                .as(TestCaseResultAggregate.class), null));

        TestVerdictBean testVerdict = mapper.toDto(testCase);
        testVerdict.setContextId(getTestCaseContextId(testCase.getTestSuiteResultId()));
        return testVerdict;
    }

    private String getTestCaseContextId(String testSuiteResultId) {
        String testSessionId = verifyFound(testSuiteResultRepository.findOne(testSuiteResultId).getTestSessionId());
        String jobId = verifyFound(testSessionRepository.findOne(testSessionId).getJobId());
        return verifyFound(jobRepository.findOne(jobId)).getContextId();
    }

}

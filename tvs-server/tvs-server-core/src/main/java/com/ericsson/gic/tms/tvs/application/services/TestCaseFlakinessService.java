package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ExecutionTime;
import com.ericsson.gic.tms.tvs.domain.model.verdict.flakiness.FlakyTestCase;
import com.ericsson.gic.tms.tvs.domain.model.verdict.flakiness.FlakyTestCaseResult;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.FlakyTestCaseResultMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseBean;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseResultBean;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.TEST_CASE_RESULT;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.*;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toMap;

@Service
public class TestCaseFlakinessService {

    private static final Set<String> MEANINGFUL_STATUS = newHashSet(
        PASSED.name(),
        "SUCCESS",
        FAILED.name(),
        BROKEN.name()
    );

    @Autowired
    private Jongo jongo;

    @Autowired
    private MongoQueryService mongoQueryService;

    @Autowired
    private FlakyTestCaseResultMapper resultMapper;

    public List<FlakyTestCaseBean> getTestCaseFlakinessByJob(String jobId,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             int limit) {
        Date startDate = DateUtils.toDate(start);
        Date endDate = DateUtils.toDate(end);
        Iterable<FlakyTestCase> testCases = jongo.getCollection(TEST_CASE_RESULT.getName())
            .aggregate(mongoQueryService.getQuery(FLAKINESS_FILTER_BY_JOB_AND_TIME), jobId, startDate, endDate)
            .and(mongoQueryService.getQuery(FLAKINESS_GROUP_BY_TEST_CASE))
            .options(mongoQueryService.optionsForLargeResults())
            .as(FlakyTestCase.class);

        return StreamSupport.stream(testCases.spliterator(), false)
            .map(this::mapTestCase)
            .sorted((a, b) -> Integer.compare(b.getFlakiness(), a.getFlakiness()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    public FlakyTestCaseBean getTestCaseFlakiness(String jobId, String testCaseName,
                                                  LocalDateTime start, LocalDateTime stop) {
        Date startDate = DateUtils.toDate(start);
        Date endDate = DateUtils.toDate(stop);
        FlakyTestCase testCase = jongo.getCollection(TEST_CASE_RESULT.getName())
            .aggregate(mongoQueryService.getQuery(
                FLAKINESS_FILTER_BY_JOB_AND_TESTCASE_AND_DATE), jobId, testCaseName, startDate, endDate)
            .and(mongoQueryService.getQuery(FLAKINESS_GROUP_BY_TEST_CASE))
            .options(mongoQueryService.optionsForLargeResults())
            .as(FlakyTestCase.class)
            .iterator()
            .next();
        return this.mapTestCase(testCase);
    }

    /**
     * Maps test case results,
     * calculating flakiness and storing latest results on the way
     */
    private FlakyTestCaseBean mapTestCase(FlakyTestCase testCase) {
        FlakyTestCaseBean testCaseBean = new FlakyTestCaseBean();
        testCaseBean.setId(testCase.getId());
        testCaseBean.setName(testCase.getName());
        testCaseBean.setTestCaseId(testCase.getTestCaseId());
        testCaseBean.setComponents(testCase.getComponentsFlat());
        testCaseBean.setSuites(testCase.getSuites());

        Multimap<String, FlakyTestCaseResultBean> results = LinkedListMultimap.create();
        int statusChanges = -1;
        int meaningfulStatusCount = 0;
        String lastStatus = null;
        long longestDuration = -1;

        List<FlakyTestCaseResult> testCaseResults = testCase.getResults();
        for (FlakyTestCaseResult testCaseResult : testCaseResults) {
            FlakyTestCaseResultBean testCaseResultBean = resultMapper.toDto(testCaseResult);
            results.put(testCaseResult.getExecutionId(), testCaseResultBean);

            String status = testCaseResult.getResultCode();
            if (status != null && MEANINGFUL_STATUS.contains(status)) {
                meaningfulStatusCount++;
                if (!status.equals(lastStatus)) {
                    lastStatus = status;
                    statusChanges++;
                }
            }

            ExecutionTime time = testCaseResult.getTime();
            if (time != null) {
                long duration = time.getDuration();
                if (duration > longestDuration) {
                    longestDuration = duration;
                }
            }
        }

        Map<String, FlakyTestCaseResultBean> latestResults = results.asMap().entrySet().stream()
            .collect(toMap(Map.Entry::getKey, entry -> {
                Collection<FlakyTestCaseResultBean> value = entry.getValue();
                FlakyTestCaseResultBean last = Iterables.getLast(value);
                if (last != null) {
                    last.setExecutionCount(value.size());
                }
                return last;
            }));

        testCaseBean.setLatestResults(latestResults);
        if (statusChanges >= 0 && meaningfulStatusCount > 0) {
            int flakiness = Math.round(statusChanges * 100f / meaningfulStatusCount);
            testCaseBean.setFlakiness(flakiness);
        } else {
            testCaseBean.setFlakiness(0);
        }
        if (longestDuration > -1) {
            testCaseBean.setLongestDuration(longestDuration);
        }

        return testCaseBean;
    }
}

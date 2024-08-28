package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.application.comparators.VersionDescendingComparator;
import com.ericsson.gic.tms.tvs.application.util.VersionConverter;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSessionAggregation;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.JobExecutionReportMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestSessionMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.JobExecutionReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode;
import com.google.common.collect.Iterables;
import ma.glasnost.orika.MapperFacade;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

@Service
public class TestSessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSessionService.class);

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Autowired
    private TestSuiteResultService testSuiteResultService;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobNotificationService jobNotificationService;

    @Autowired
    private TestSessionMapper testSessionMapper;

    @Autowired
    private JobExecutionReportMapper jobExecutionReportMapper;

    @Autowired
    private Jongo jongo;

    @Autowired
    private MongoQueryService mongoQueryService;

    @Autowired
    private JobRepository jobRepository;


    @Autowired
    private MapperFacade mapperFacade;

    public Page<TestSessionBean> getPaginatedTestSessions(String jobId, LocalDateTime startTime,
                                                          LocalDateTime endTime, Pageable pageRequest, Query query) {
        Page<TestSession> page;
        if (startTime == null || endTime == null) {
            page = testSessionRepository.findByJobId(jobId, pageRequest, query);
        } else {
            page = testSessionRepository.findByJobIdAndDate(jobId, startTime, endTime, pageRequest, query);
        }
        return testSessionMapper.mapAsPage(page, pageRequest);
    }

    public TestSessionBean getTestSession(String jobId, String executionId) {
        TestSession testSession = verifyFound(getTestSessionEntity(jobId, executionId));
        return testSessionMapper.toDto(testSession);
    }

    public List<TestSession> getAllTestSessions(String jobId) {
        return testSessionRepository.findByJobId(jobId);
    }

    public List<JobExecutionReport> findJobExecutions(String jobId) {
        List<TestSession> jobExecutions = testSessionRepository.findByJobId(jobId);
        return jobExecutionReportMapper.mapAsList(jobExecutions);
    }

    public TestSession getTestSessionEntity(String jobId, String executionId) {
        return testSessionRepository.findByExecutionIdAndJobId(executionId, jobId);
    }

    public List<String> getTestSessionChildren(String jobId, String executionId) {
        String testSessionId = getTestSessionEntity(jobId, executionId).getId();
        return testSuiteResultService.getSuiteIds(testSessionId);
    }

    public TestSessionBean updateTestSession(ResultPath parentResultPath,
                                             String executionId,
                                             TestSessionBean bean
    ) {
        TestSession testSession = getTestSessionEntity(parentResultPath.getJobId(), executionId);

        if (testSession == null) {
            testSession = new TestSession();
            testSession.setJobId(parentResultPath.getJobId());
            testSession.setExecutionId(executionId);
            testSession = testSessionRepository.save(testSession);
        }

        boolean ignoredFlagChanged = bean.isIgnored() != testSession.isIgnored();
        updateTestSession(parentResultPath, executionId, bean, testSession);

        ResultPath resultPath = parentResultPath.withTestSession(executionId, testSession.getId());
        List<TestSuiteResultBean> testSuiteResultBeans =
            testSuiteResultService.updateTestSuites(resultPath, bean.getTestSuites());

        for (TestSuiteResultBean suite : testSuiteResultBeans) {
            testSuiteResultService.aggregateTestSuite(parentResultPath.getJobId(),
                executionId,
                suite.getName());
        }

        if (ignoredFlagChanged) {
            testCaseResultRepository.setIgnoredByExecutionId(executionId, bean.isIgnored());
            LOGGER.info("Ignored flag updated for test session: executionId={}, ignored={}",
                testSession.getExecutionId(), bean.isIgnored());
        }

        TestSessionBean testSessionBean = aggregateTestSession(parentResultPath.getJobId(), executionId, SELF);
        testSessionBean.setTestSuites(testSuiteResultBeans);

        if (!testSession.isIgnored()) {
            jobNotificationService.verifyJobExecution(resultPath.getJobId(), executionId);
        }

        return testSessionBean;
    }

    private TestSessionBean updateTestSession(ResultPath resultPath,
                                              String executionId,
                                              TestSessionBean bean,
                                              TestSession existingSession) {
        testSessionMapper.copyFields(bean, existingSession);
        existingSession.setExecutionId(executionId);
        existingSession.setJobId(resultPath.getJobId());

        String isoVersion = (String) existingSession.getAdditionalField("ISO_VERSION");
        String paddedIsoVersion = VersionConverter.getPaddedVersion(isoVersion);
        if (paddedIsoVersion != null) {
            existingSession.addAdditionalFields("ISO_VERSION_PADDED", paddedIsoVersion);
        }

        TestSession testSession = testSessionRepository.save(existingSession);
        TestSessionBean testSessionBean = testSessionMapper.toDto(testSession);
        testSessionBean.setTestSuites(bean.getTestSuites());

        return testSessionBean;
    }

    public List<TestSessionBean> updateTestSessions(ResultPath resultPath, List<TestSessionBean> testSessions) {
        if (testSessions == null) {
            return emptyList();
        }
        return testSessions.stream()
            .map(testSessionBean -> updateTestSession(resultPath, testSessionBean.getId(), testSessionBean))
            .collect(toList());
    }

    public List<String> getSuiteIds(String jobId) {
        return testSessionRepository.findByJobId(jobId)
            .stream()
            .map(TestSession::getExecutionId)
            .collect(Collectors.toList());
    }

    public TestSessionBean aggregateTestSession(String jobId, String executionId, TraversalMode mode) {

        TestSessionBean testSession;

        if (mode == DESCENDING || mode == WIDE) {
            TestSession entity = verifyFound(getTestSessionEntity(jobId, executionId));
            testSuiteResultService.getAllTestSuites(entity.getId()).forEach(testSuiteResult ->
                retriggerTestSuite(jobId, executionId, testSuiteResult.getName()));
        }

        testSession = aggregateTestSession(jobId, executionId);

        if (mode == ASCENDING || mode == WIDE) {
            jobService.aggregateJob(jobId);
        }

        return testSession;
    }

    public TestSessionBean aggregateTestSession(String jobId, String executionId) {
        TestSession testSession = getTestSessionEntity(jobId, executionId);

        TestSessionAggregation aggregated =
            Iterables.getFirst(jongo.getCollection(TEST_SUITE_RESULT.getName())
                .aggregate(mongoQueryService.getQuery(TEST_SESSION_FIELDS_SUITE_MATCH), testSession.getId())
                .and(mongoQueryService.getQuery(TEST_SESSION_FIELDS_SUITE_SORT))
                .and(mongoQueryService.getQuery(TEST_SESSION_FIELDS_SUITE_GROUP))
                .and(mongoQueryService.getQuery(TEST_SESSION_FIELDS_SUITE_PROJECT))
                .as(TestSessionAggregation.class), null);

        if (aggregated != null) {
            mapperFacade.map(aggregated, testSession);
            testSession = testSessionRepository.save(testSession);
        }

        return testSessionMapper.toDto(testSession);
    }

    public List<String> getAllIsoVersions(List<String> contexts) {
        List<String> jobUids = jobRepository.findListByContextIdIn(contexts, null).stream()
            .map(Job::getUid)
            .collect(Collectors.toList());

        List<String> allIsoVersions = jongo.getCollection(TEST_SESSION.getName())
            .distinct(ISO_VERSION)
            .query("{jobId: {$in:#}, ignored: {$ne: true}}", jobUids)
            .as(String.class);

        Collections.sort(allIsoVersions, new VersionDescendingComparator());

        return allIsoVersions;
    }

    private void retriggerTestSuite(String jobId, String executionId, String testSuiteName) {
        try {
            testSuiteResultService.aggregateTestSuite(jobId, executionId, testSuiteName);
        } catch (NotFoundException e) {
            // Happens in case when there are no child test cases. Should be ignored
        }
    }
}

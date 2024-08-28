package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.presentation.dto.ContextBean;
import com.ericsson.gic.tms.presentation.resources.ContextResource;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.model.verdict.JobAggregation;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.domain.util.EqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.NotEqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.JobMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.JobReportMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.JobReport;
import com.ericsson.gic.tms.tvs.presentation.dto.JobStatisticsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ma.glasnost.orika.MapperFacade;
import org.jongo.Aggregate;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.*;
import static com.google.common.base.MoreObjects.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static java.util.stream.Collectors.*;

@Service
public class JobService {

    @Autowired
    private Jongo jongo;

    @Autowired
    private MongoQueryService mongoQueryService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private JobReportMapper jobReportMapper;

    @Autowired
    private ContextResource contextResource;

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Autowired
    private MapperFacade mapperFacade;

    private static final String COMMA = ",";
    private static final String ISO_VERSION = "Iso Version";
    private static final String DROP_NAME = "Drop";

    public Page<JobBean> getPaginatedJobs(String contextId, Pageable pageRequest, Query query) {
        Page<Job> jobs;
        if (isNullOrEmpty(contextId)) {
            jobs = jobRepository.findAll(pageRequest, query);
        } else {
            List<String> contextIds = contextResource.getChildren(contextId).unwrap().stream()
                .map(ContextBean::getId)
                .collect(toList());
            jobs = jobRepository.findByContextIdIn(contextIds, pageRequest, query);
        }
        return jobMapper.mapAsPage(jobs, pageRequest);
    }

    public List<JobReport> findJobsByContextId(String contextId) {
        List<Job> jobList;
        if (isNullOrEmpty(contextId)) {
            jobList = newArrayList(jobRepository.findAll());
        } else {
            List<String> contextIds = contextResource.getChildren(contextId).unwrap().stream()
                .map(ContextBean::getId)
                .collect(toList());
            jobList = jobRepository.findByContextIdIn(contextIds);
        }
        return jobReportMapper.mapAsList(jobList);
    }

    public JobBean getJob(String jobId) {
        Job job = verifyFound(jobRepository.findOne(jobId));
        return jobMapper.toDto(job);
    }

    public Iterable<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<String> findJobIdsFromNames(String jobNames) {
        List<String> jobNamesList = Lists.newArrayList(jobNames.split(COMMA));
        List<Job> allJobs = Lists.newArrayList(jobRepository.findAll());
        List<String> jobIds = new ArrayList<>();

        for (String jobName : jobNamesList) {
            String jobId = findJobId(allJobs, jobName);
            if (!isNullOrEmpty(jobId)) {
                jobIds.add(jobId);
            }
        }
        return jobIds;
    }

    public List<String> getJobChildren(String jobId) {
        return testSessionService.getSuiteIds(jobId);
    }

    public JobBean updateJob(String contextId, String jobName, JobBean bean) {
        boolean isNewJob = false;
        Job job = jobRepository.findByNameAndContextId(jobName, contextId);
        String newContext = firstNonNull(bean.getContext(), contextId);

        if (job == null) {
            job = new Job();
            isNewJob = true;
        } else {
            if (!Objects.equals(contextId, newContext)) {
                testCaseResultRepository.updateContextIdByJobId(newContext, job.getUid());
            }
        }
        jobMapper.copyFields(bean, job);
        job.setContextId(newContext);
        job.setName(jobName);

        Job saved = jobRepository.save(job);

        if (isNewJob) {
            job.setUid(saved.getUid());
        }

        ResultPath resultPath = new ResultPath()
            .withJob(contextId, job.getUid());
        List<TestSessionBean> testSessionBeans =
            testSessionService.updateTestSessions(resultPath, bean.getTestSessions());

        JobBean jobBean;
        if (testSessionBeans.isEmpty()) {
            jobBean = jobMapper.toDto(saved);
        } else {
            jobBean = aggregateJob(job.getUid());
        }
        jobBean.setTestSessions(testSessionBeans);

        return jobBean;
    }

    public List<JobStatisticsBean> aggregateTestSessions(
        String jobName, Query query, Integer limit, String orderBy) {
        query.addFilter(new EqualsFilter("jobId", jobName));
        query.addFilter(new NotEqualsFilter("ignored", true));

        Aggregate aggregate = jongo.getCollection("testSession")
            .aggregate("{$match: " + query.toString() + "}", query.getQueryParameters().toArray());

        if (limit != null) {
            aggregate = aggregate
                .and("{$sort: {'time.stopDate': -1}}")
                .and("{$limit: #}", limit);
        }

        Aggregate.ResultsIterator<SessionId> result = aggregate
            .and("{$group: {_id: \"$executionId\", id: {$first: \"$_id\"}}}")
            .as(SessionId.class);

        Set<String> sessionIds = StreamSupport.stream(result.spliterator(), false)
            .map(SessionId::getId)
            .collect(toSet());

        String matching = mongoQueryService.getQuery(JOB_STATISTICS_MATCHING);
        String aggregation = mongoQueryService.getQuery(JOB_STATISTICS_AGGREGATION);

        aggregate = jongo.getCollection("testSuiteResult")
            .aggregate(matching, sessionIds);

        String sort;
        if (orderBy.equals(ISO_VERSION)) {
            sort = "{$sort: {ISO_VERSION_PADDED: 1}}";
        } else {
            sort = "{$sort: {time: 1}}";
        }

        aggregate = aggregate.and(aggregation)
            .and(sort);

        Aggregate.ResultsIterator<JobStatisticsBean> testSuite =
            aggregate.as(JobStatisticsBean.class);

        List<JobStatisticsBean> statistics = newArrayList(testSuite.iterator());

        // Ascending sort will not work for this so must be reversed here
        if (orderBy.equals(ISO_VERSION)) {
            Collections.reverse(statistics);
        }

        if (orderBy.equals(DROP_NAME)) {
            Collections.sort(statistics, new Comparator<JobStatisticsBean>() {
                public int compare(JobStatisticsBean o1, JobStatisticsBean o2) {
                    Double drop1 = Double.parseDouble(o1.getDropName());
                    Double drop2 = Double.parseDouble(o2.getDropName());
                    return drop1.compareTo(drop2);
                }
            });
        }
        return statistics;
    }

    public JobBean aggregateJob(String jobId) {
        Job job = verifyFound(jobRepository.findOne(jobId));

        JobAggregation jobAggregation = verifyFound(Iterables.getFirst(jongo.getCollection(TEST_SESSION.getName())
            .aggregate(mongoQueryService.getQuery(JOB_FIELDS_SESSION_MATCH), jobId)
            .and(mongoQueryService.getQuery(JOB_FIELDS_SESSION_SORT))
            .and(mongoQueryService.getQuery(JOB_FIELDS_SESSION_GROUP))
            .as(JobAggregation.class), null));
        mapperFacade.map(jobAggregation, job);
        return jobMapper.toDto(jobRepository.save(job));
    }

    public void moveJobToContext(String jobId, String contextId) {
        Job job = verifyFound(jobRepository.findOne(jobId));

        job.setContextId(contextId);
        jobRepository.save(job);

        testCaseResultRepository.updateContextIdByJobId(contextId, jobId);
    }

    private static class SessionId {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    private String findJobId(List<Job> allJobs, String jobName) {
        for (Job job : allJobs) {
            if (job.getName().equals(jobName)) {
                return job.getUid();
            }
        }
        return null;
    }
}

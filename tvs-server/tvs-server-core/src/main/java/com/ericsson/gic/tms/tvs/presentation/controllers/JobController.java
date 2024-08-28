package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.CollectionMetadataService;
import com.ericsson.gic.tms.tvs.application.services.JobService;
import com.ericsson.gic.tms.tvs.application.services.QueryService;
import com.ericsson.gic.tms.tvs.application.services.SortService;
import com.ericsson.gic.tms.tvs.application.services.TestSessionService;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.presentation.utility.ChartParameters;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.JobReport;
import com.ericsson.gic.tms.tvs.presentation.dto.JobStatisticsBean;
import com.ericsson.gic.tms.tvs.presentation.resources.JobResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.COLUMNS;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.JOB;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;

@Controller
public class JobController extends AbstractJsonApiCapableController implements JobResource {

    private static final String CONTENT_DISPOSITION_VALUE = "inline; filename=\"context-activities-%s.csv\"";

    @Autowired
    private JobService jobService;

    @Autowired
    private CollectionMetadataService collectionMetadataService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private SortService sortService;

    @Autowired
    private TestSessionService testSessionService;

    @Override
    public DocumentList<JobBean> getList(String contextId,
                                         int page, int size, String orderBy, SortingMode orderMode, String query) {
        Query queryList = queryService.createQuery(query);

        PageRequest pageRequest = new PageRequest(page - 1, size, sortService.getSort(orderMode.toString(), orderBy));

        Page<JobBean> jobs = jobService.getPaginatedJobs(contextId, pageRequest, queryList);

        return responseFor(jobs.getContent())
            .withSelfRel(JobResource.class)
            .withPagination(JobResource.class, jobs, pageRequest)
            .withMeta(COLUMNS, collectionMetadataService.getColumns(JOB.getName()))
            .build();
    }

    @Override
    public DocumentList<JobReport> exportJobs(String contextId) {
        return responseFor(jobService.findJobsByContextId(contextId))
            .withSelfRel(JobResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_VALUE, contextId))
            .build();
    }

    @Override
    public Document<JobBean> getJob(String jobId) {
        return responseFor(jobService.getJob(jobId))
            .withSelfRel(JobResource.class, JOB_ID)
            .withMeta("children", jobService.getJobChildren(jobId))
            .build();
    }

    @Override
    public DocumentList<JobStatisticsBean> getStatistics(String jobId, String query, Integer limit, String orderBy) {
        String parsedOrderBy = ChartParameters.getChartParamByValue(orderBy);
        Query parsedQuery = queryService.createQuery(query);
        return responseFor(jobService.aggregateTestSessions(jobId, parsedQuery, limit, parsedOrderBy))
            .withSelfRel(JobResource.class, STATISTICS)
            .build();
    }

    @Override
    public Document<JobBean> aggregateJob(String jobId) {
        return responseFor(jobService.aggregateJob(jobId))
            .withSelfRel(JobResource.class, AGGREGATE_JOB)
            .build();
    }
}

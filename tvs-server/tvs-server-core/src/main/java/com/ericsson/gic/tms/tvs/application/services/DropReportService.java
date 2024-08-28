package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.application.comparators.DropNameComparator;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop.JobCumulativeDropReport;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop.JobDropReport;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop.JobLastISODropReport;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.CumulativeDropReportMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.LastISODropReportMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportType;
import org.jongo.Aggregate;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.*;
import static com.ericsson.gic.tms.tvs.application.util.PaginationUtil.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.domain.repositories.BaseJongoRepository.*;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.*;
import static com.google.common.base.MoreObjects.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static java.time.LocalDateTime.*;
import static java.util.Collections.*;

@Service
public class DropReportService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private Jongo jongo;

    @Autowired
    private LastISODropReportMapper isoDropReportMapper;

    @Autowired
    private CumulativeDropReportMapper cumulativeDropReportMapper;

    @Autowired
    private MongoQueryService mongoQueryService;

    @Autowired
    private TestSessionRepository testSessionRepository;

    private DropNameComparator dropNameComparator;

    @PostConstruct
    public void init() {
        dropNameComparator = new DropNameComparator();
    }

    public Page<? extends DropReportBean> getPaginatedDropReportData(String jobId, LocalDateTime since,
                                                                     LocalDateTime until, Pageable pageRequest,
                                                                     Query query, DropReportType dropReportType) {
        verifyFound(jobRepository.findOne(jobId));
        LocalDateTime dateFrom = firstNonNull(since, from(now().minusMonths(1)));
        LocalDateTime dateTo = firstNonNull(until, now());

        switch (dropReportType) {
            case LAST_ISO:
                Aggregate allDropResult = prepareLastIsoReportPipeline(jobId, dateFrom, dateTo);
                Iterable<JobLastISODropReport> filteredDropResult =
                    finalizeResult(allDropResult, pageRequest, query, JobLastISODropReport.class);

                return isoDropReportMapper.mapAsPage((Page<JobLastISODropReport>) filteredDropResult, pageRequest);
            case CUMULATIVE:
                Aggregate pipeline = prepareCumulativeReportPipeline(jobId, dateFrom, dateTo);
                Iterable<JobCumulativeDropReport> resultsList =
                    finalizeResult(pipeline, pageRequest, query, JobCumulativeDropReport.class);

                setTestSessionCount(resultsList);
                return cumulativeDropReportMapper.mapAsPage((Page<JobCumulativeDropReport>) resultsList, pageRequest);
            default:
                throw new IllegalArgumentException("Not supported drop report type: " + dropReportType);
        }
    }

    public List<? extends DropReportBean> getAllDropReportData(String jobId, LocalDateTime since, LocalDateTime until,
                                                               Query query, DropReportType dropReportType) {
        verifyFound(jobRepository.findOne(jobId));

        switch (dropReportType) {
            case LAST_ISO:
                Aggregate dropResultList = prepareLastIsoReportPipeline(jobId, since, until);
                List<JobLastISODropReport> result =
                    newArrayList(finalizeResult(dropResultList, null, query, JobLastISODropReport.class));
                sort(result, dropNameComparator);
                return isoDropReportMapper.mapAsList(result);
            case CUMULATIVE:
                Aggregate pipeline = prepareCumulativeReportPipeline(jobId, since, until);
                List<JobCumulativeDropReport> resultList =
                    newArrayList(finalizeResult(pipeline, null, query, JobCumulativeDropReport.class));
                sort(resultList, dropNameComparator);
                setTestSessionCount(resultList);
                return cumulativeDropReportMapper.mapAsList(resultList);
            default:
                throw new IllegalArgumentException("Not supported drop report type: " + dropReportType);
        }
    }

    private Aggregate prepareLastIsoReportPipeline(String jobId, LocalDateTime since, LocalDateTime until) {
        return jongo.getCollection(TEST_SESSION.getName())
            .aggregate(mongoQueryService.getQuery(DROP_REPORT_AGGREGATION_MATCH),
                jobId,
                DateUtils.toDate(since),
                DateUtils.toDate(until))
            .and(mongoQueryService.getQuery(DROP_REPORT_AGGREGATION_SORT))
            .and(mongoQueryService.getQuery(DROP_REPORT_AGGREGATION_GROUP));
    }

    private Aggregate prepareCumulativeReportPipeline(String jobId, LocalDateTime since,
                                                      LocalDateTime until) {
        return jongo.getCollection(TEST_CASE_RESULT.getName())
            .aggregate(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_MATCH),
                jobId,
                DateUtils.toDate(since),
                DateUtils.toDate(until))
            .and(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_INITIAL_SORT))
            .and(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_GROUP_RESULT_SET))
            .and(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_UNWIND))
            .and(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_MATCH_BY_RESULT))
            .and(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_GROUP_LAST_RESULT))
            .and(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_GROUP_PRE_PROJECT))
            .and(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_PROJECT))
            .and(mongoQueryService.getQuery(DROP_REPORT_CUMULATIVE_FINAL_SORT));
    }

    private <T extends JobDropReport> Iterable<T> finalizeResult(Aggregate aggregationPipeline,
                                                                 Pageable pageable,
                                                                 Query query,
                                                                 Class<T> targetClass) {
        if (query != null && !query.getFilters().isEmpty()) {
            aggregationPipeline.and("{$match: " + query.toString() + "}", query.getQueryParameters().toArray());
        }

        if (pageable != null) {
            String sortParamJson = getMongoSortString(pageable);

            if (!isNullOrEmpty(sortParamJson)) {
                aggregationPipeline.and("{$sort: " + sortParamJson + "}");
            }
        }

        List<T> result = newArrayList(aggregationPipeline.as(targetClass).iterator());

        if (pageable != null) {
            List<T> paginatedItems = getPaginatedItems(result, pageable.getPageNumber(), pageable.getPageSize());
            return new PageImpl<>(paginatedItems, pageable, result.size());
        }

        return result;
    }

    private void setTestSessionCount(Iterable<JobCumulativeDropReport> beanList) {
        beanList.forEach(bean -> {
            long count = testSessionRepository.countByDropName(bean.getDropName());
            bean.setTestSessionsCount(Long.valueOf(count).intValue());
        });
    }
}

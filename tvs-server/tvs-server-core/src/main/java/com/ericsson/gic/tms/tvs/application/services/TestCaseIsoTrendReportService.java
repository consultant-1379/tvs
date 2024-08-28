package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.application.comparators.TestCaseIsoAscComparator;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.TestCaseResultTrendReport;
import com.ericsson.gic.tms.tvs.domain.util.AndFilter;
import com.ericsson.gic.tms.tvs.domain.util.Filter;
import com.ericsson.gic.tms.tvs.domain.util.MongoExpression;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import ma.glasnost.orika.MapperFacade;
import org.jongo.Aggregate;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.TEST_CASE_RESULT;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.sort;

@Service
public class TestCaseIsoTrendReportService {

    @Autowired
    private Jongo jongo;

    @Autowired
    private MongoQueryService mongoQueryService;

    @Autowired
    private MapperFacade mapperFacade;

    private TestCaseIsoAscComparator testCaseIsoAscComparator;

    @PostConstruct
    protected void init() {
        testCaseIsoAscComparator = new TestCaseIsoAscComparator();
    }

    private static final String GROUP_BY = "groupBy";


    public List<TestCaseResultTrendReport> aggregateByTag(List<String> contextIds, List<String> jobIds,
                                                          List<String> isoVersions, String tag, Query query) {

        Aggregate pipeline = jongo.getCollection(TEST_CASE_RESULT.getName())
            .aggregate(mongoQueryService.getQuery(TC_RESULTS_TREND_MATCH), isoVersions, jobIds, contextIds, tag);

        addMatch(pipeline, getFilter(query, GROUP_BY, tag)); //FIXME: does not work if field is array (like GROUPS)
        addMatch(pipeline, getFilter(query, JOB_ID));

        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_PROJECT), tag);
        if (isArrayField(tag)) {
            pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_UNWIND_TAG), "$" + tag);
        }
        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_GROUP_BY_ISO_AND_TAG), "$" + tag);
        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_PROJECT_PASS_RATE));

        if (hasFilter(query, RESULT_CODE)) {
            filterGroupsByResultCode(pipeline, getFilter(query, RESULT_CODE));
        }

        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_GROUP_BY_ISO));
        List<TestCaseResultTrendReport> data = newArrayList(pipeline.as(TestCaseResultTrendReport.class).iterator());

        return sortAsc(data);
    }

    /**
     * Return groups having test cases with specified result codes in given ISO range
     */
    private void filterGroupsByResultCode(Aggregate pipeline, Query resultCodeFilter) {

        // check each group for presence of any of the result codes
        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_RC_PROJECT_IN_GROUP),
            resultCodeFilter.getQueryParameters().toArray());

        // group results by tag
        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_RC_GROUP_BY_TAG));

        // filter tags having result codes in every iso
        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_RC_PROJECT_IN_TAG));
        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_RC_MATCH));

        // restore original grouping
        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_RC_UNWIND_DATA));
        pipeline.and(mongoQueryService.getQuery(TC_RESULTS_TREND_RC_GROUP_BY_ISO_AND_TAG));
    }

    private void addMatch(Aggregate pipeline, Query query) {
        if (query != null && !query.getFilters().isEmpty()) {
            pipeline.and("{$match: " + query.toString() + "}", query.getQueryParameters().toArray());
        }
    }

    private Query getFilter(Query filters, String field) {
        return getFilter(filters, field, field);
    }

    private Query getFilter(Query filters, String dtoField, String collectionField) {
        Optional<Filter> maybeFilter = getFilterByFieldName(filters.getFilters(), dtoField);
        if (maybeFilter.isPresent()) {
            Filter filter = maybeFilter.get();
            filter.setField(collectionField);
            return new Query(filter);
        }
        return new Query();
    }

    private boolean hasFilter(Query query, String fieldName) {
        return getFilterByFieldName(query.getFilters(), fieldName).isPresent();
    }

    private Optional<Filter> getFilterByFieldName(List<MongoExpression> filters, String fieldName) {
        for (MongoExpression expression : filters) {
            if (expression instanceof Filter) {
                Filter filter = (Filter) expression;
                if (filter.getField().equals(fieldName)) {
                    return Optional.of(filter);
                }
            } else if (expression instanceof AndFilter) {
                AndFilter filter = (AndFilter) expression;
                List<MongoExpression> subList = newArrayList();
                subList.addAll(filter.getFilters());
                return getFilterByFieldName(subList, fieldName);
            }
        }
        return Optional.empty();
    }


    private List<TestCaseResultTrendReport> sortAsc(List<TestCaseResultTrendReport> data) {
        sort(data, testCaseIsoAscComparator);
        return data;
    }
}

package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.verdict.ImportStatus;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.domain.util.EqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.GreaterThanFilter;
import com.ericsson.gic.tms.tvs.domain.util.LessThanFilter;
import com.ericsson.gic.tms.tvs.domain.util.NotEqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import org.jongo.Find;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.google.common.collect.Lists.*;

@Repository
public class TestCaseResultRepository extends ExtensibleJongoRepository<TestCaseResult, String> {

    private static final String ALL = "{}";

    public TestCaseResultRepository() {
        super(TestCaseResult.class);
    }

    @Override
    protected String getCollectionName() {
        return TEST_CASE_RESULT.getName();
    }

    public Page<TestCaseResult> findByTestCaseId(String testCaseId, Pageable pageable) {
        return findBy("{testCaseId: #}", pageable, testCaseId);
    }

    public TestCaseResult findByTestCaseId(String testCaseId) {
        return findOneBy("{testCaseId: #}", testCaseId);
    }

    public List<TestCaseResult> findByJobExecutionId(String jobExecutionId) {
        return newArrayList(findBy("{executionId: #}", jobExecutionId));
    }

    public List<TestCaseResult> findByJobExecutionAndSuite(String jobExecutionId, String suiteName) {
        return newArrayList(findBy("{executionId: #, testSuiteName: #}", jobExecutionId, suiteName));
    }

    public List<TestCaseResult> findByJobExecutionAndSuites(String jobExecutionId, List<String> suiteNames) {
        return newArrayList(findBy("{executionId: #, testSuiteName: {$in: #}}", jobExecutionId, suiteNames));
    }

    public List<TestCaseResult> findByName(String name) {
        Query query = new Query(new EqualsFilter("name", name));
        return newArrayList(findBy(query));
    }

    public Page<TestCaseResult> findByName(String name, Date startTime, Date endTime, Pageable pageable, Query query) {
        Query filters = cloneQuery(query)
                .addFilter(new EqualsFilter("name", name));

        if (startTime != null && endTime != null) {
            filters.addFilter(new GreaterThanFilter("time.startDate", startTime))
                   .addFilter(new LessThanFilter("time.stopDate", endTime));
        }
        return findBy(filters, pageable);
    }

    public Page<TestCaseResult> findByImportStatus(ImportStatus status, Pageable pageable) {
        return findBy("{importStatus: #}", pageable, status);
    }

    public Page<TestCaseResult> findByTestSuiteResultId(String testSuiteResultId, Pageable pageable, Query query) {
        Query filters = cloneQuery(query)
            .addFilter(new EqualsFilter("testSuiteResultId", testSuiteResultId));
        return findBy(filters, pageable);
    }

    public List<TestCaseResult> findByTestSuiteResultId(String testSuiteResultId) {
        return newArrayList(findBy("{testSuiteResultId: #}", testSuiteResultId));
    }

    public TestCaseResult findByTestCaseIdAndAndTestSuiteResultId(String testCaseId, String testSuiteResultId) {
        return findOneBy("{testCaseId: #, testSuiteResultId: #}", testCaseId, testSuiteResultId);
    }

    public List<TestCaseResult> findByJobId(String jobId) {
        return newArrayList(findBy("{jobId: #}", jobId));
    }

    public void updateContextIdByJobId(String contextId, String jobId) {
        getCollection()
            .update("{jobId: #}", jobId)
            .multi()
            .with("{$set: {contextId: #}}", contextId);
    }

    public void setIgnoredByExecutionId(String executionId, boolean ignored) {
        getCollection()
            .update("{executionId: #}", executionId)
            .multi()
            .with("{$set: {ignored: #}}", ignored);
    }

    @Override
    protected Page<TestCaseResult> findBy(String query, Pageable pageable, Object... args) {
        return super.findBy(filter(query), pageable, args);
    }

    @Override
    protected Iterable<TestCaseResult> findBy(String query, Object... args) {
        return super.findBy(filter(query), args);
    }


    @Override
    protected TestCaseResult findOneBy(String query, Object... args) {
        return super.findOneBy(filter(query), args);
    }

    @Override
    protected Iterable<TestCaseResult> findBy(Query query) {
        return super.findBy(filter(query));
    }

    @Override
    protected Page<TestCaseResult> findBy(Query q, Pageable pageable) {
        return super.findBy(filter(q), pageable);
    }

    @Override
    protected TestCaseResult findOneBy(Query query) {
        return super.findOneBy(filter(query));
    }

    @Override
    public Page<TestCaseResult> findAll(Pageable pageable) {
        Find find = getCollection().find(filter(ALL));
        return paginate(find, pageable, this::count);
    }

    @Override
    public long count() {
        return super.count(filter(ALL));
    }

    @Override
    public long count(String query, Object... args) {
        return super.count(filter(query), args);
    }

    private String filter(String query) {
        int index = query.lastIndexOf("}");
        if (index >= 0) {
            StringBuilder builder = new StringBuilder(query);
            if (index > 0 && query.charAt(index - 1) != '{') {
                builder.insert(index++, ',');
            }
            return builder.insert(index, "ignored: {$ne: true}").toString();
        }
        return query;
    }

    private Query filter(Query q) {
        q.addFilter(new NotEqualsFilter("ignored", true));
        return q;
    }
}

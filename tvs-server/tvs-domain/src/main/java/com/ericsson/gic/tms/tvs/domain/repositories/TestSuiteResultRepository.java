package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.domain.util.EqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.google.common.collect.Lists.*;

@Repository
public class TestSuiteResultRepository extends BaseJongoRepository<TestSuiteResult, String> {

    public TestSuiteResultRepository() {
        super(TestSuiteResult.class, "id");
    }

    @Override
    protected String getCollectionName() {
        return TEST_SUITE_RESULT.getName();
    }

    public Page<TestSuiteResult> findByTestSessionId(String testSessionId, Pageable pageable, Query q) {
        Query filters = cloneQuery(q)
            .addFilter(new EqualsFilter("testSessionId", testSessionId));
        return findBy(filters, pageable);
    }

    public List<TestSuiteResult> findByTestSessionIds(List<String> testSessionIds) {
        return newArrayList(findBy("{testSessionId: {$in: #}}", testSessionIds));
    }

    public List<TestSuiteResult> findByTestSessionId(String testSessionId) {
        return newArrayList(findBy("{testSessionId: #}", testSessionId));
    }

    public TestSuiteResult findByTestSessionIdAndName(String testSessionId, String name) {
        return findOneBy("{testSessionId: #, name: #}", testSessionId, name);
    }

}

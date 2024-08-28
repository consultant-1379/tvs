package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.util.EqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.GreaterThanFilter;
import com.ericsson.gic.tms.tvs.domain.util.LessThanFilter;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.google.common.collect.Lists.*;

@Repository
public class TestSessionRepository extends ExtensibleJongoRepository<TestSession, String> {

    public TestSessionRepository() {
        super(TestSession.class, "id");
    }

    @Override
    protected String getCollectionName() {
        return TEST_SESSION.getName();
    }

    public Page<TestSession> findByJobIdAndDate(String jobId, LocalDateTime start,
                                         LocalDateTime stop, Pageable pageable, Query query) {
        Date startDate = DateUtils.toDate(start);
        Date endDate = DateUtils.toDate(stop);

        Query filters = cloneQuery(query)
            .addFilter(new EqualsFilter("jobId", jobId))
            .addFilter(new GreaterThanFilter("time.startDate", startDate))
            .addFilter(new LessThanFilter("time.stopDate", endDate));
        return findBy(filters, pageable);
    }

    public Page<TestSession> findByJobId(String jobId, Pageable pageable, Query query) {
        Query filters = cloneQuery(query)
                .addFilter(new EqualsFilter("jobId", jobId));
        return findBy(filters, pageable);
    }

    public TestSession findByExecutionId(String executionId) {
        return findOneBy("{executionId: #}", executionId);
    }

    public TestSession findByExecutionId(String executionId, Query query) {
        Query filters = cloneQuery(query)
            .addFilter(new EqualsFilter("executionId", executionId));
        return findOneBy(filters);
    }

    public List<TestSession> findByJobId(String jobId) {
        return newArrayList(findBy("{jobId: #}", jobId));
    }

    /**
     * Use #findByJobIdAndExecutionId instead
     * @deprecated should not be used
     */
    @Deprecated
    public TestSession findByExecutionIdAndJobId(String executionId, String jobId) {
        return findByJobIdAndExecutionId(jobId, executionId);
    }

    public long countByDropName(String dropName) {
        return count("{DROP_NAME: #}", dropName);
    }

    public TestSession findByJobIdAndExecutionId(String jobId, String executionId) {
        return findOneBy("{jobId: #, executionId: #}", jobId, executionId);
    }

    public List<TestSession> findAllByJobIdAndExecutionIds(String jobId, List<String> executionIds) {
        return newArrayList(findBy("{jobId: #, executionId: {$in: #}}", jobId, executionIds));
    }
}

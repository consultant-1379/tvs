package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.TvsCollections;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.util.EqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.InFilter;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.google.common.collect.Lists.*;

@Repository
public class JobRepository extends ExtensibleJongoRepository<Job, String> {

    public JobRepository() {
        super(Job.class, "uid");
    }

    @Override
    protected String getCollectionName() {
        return TvsCollections.JOB.getName();
    }

    public Page<Job> findAll(Pageable page, Query query) {
        Query filters = cloneQuery(query);
        return findBy(filters, page);
    }

    public Page<Job> findByContextIdIn(List<String> contextId, Pageable page, Query query) {
        Query filters = cloneQuery(query)
            .addFilter(new InFilter("contextId", contextId));
        return findBy(filters, page);
    }

    public List<Job> findByContextIdIn(List<String> contextId) {
        return newArrayList(findBy(new Query(new InFilter("contextId", contextId))));
    }

    public List<Job> findListByContextIdIn(List<String> contextId, Query query) {
        Query filters = cloneQuery(query)
            .addFilter(new InFilter("contextId", contextId));
        return newArrayList(findBy(filters));
    }

    public Job findByNameAndContextId(String name, String contextId) {
        Query filters = new Query()
            .addFilter(new EqualsFilter("contextId", contextId))
            .addFilter(new EqualsFilter("name", name));
        return findOneBy(filters);
    }
}

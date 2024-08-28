package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.TvsCollections;
import com.ericsson.gic.tms.tvs.domain.model.verdict.JobConfiguration;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.google.common.collect.Lists.*;

@Repository
public class JobConfigurationRepository extends BaseJongoRepository<JobConfiguration, String> {

    protected JobConfigurationRepository() {
        super(JobConfiguration.class);
    }

    @Override
    protected String getCollectionName() {
        return TvsCollections.JOB_CONFIGURATION.getName();
    }

    public List<JobConfiguration> findBySource(String source) {
        return newArrayList(findBySourceAndOrderByPriority(source));
    }

    private Iterable<JobConfiguration> findBySourceAndOrderByPriority(String source) {
        return getCollection()
            .find("{source: #}", source)
            .sort("{priority: -1}")
            .as(JobConfiguration.class);
    }
}

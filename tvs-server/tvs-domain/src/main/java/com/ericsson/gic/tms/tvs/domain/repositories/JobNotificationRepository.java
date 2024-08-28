package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.TvsCollections;
import com.ericsson.gic.tms.tvs.domain.model.verdict.notification.JobNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobNotificationRepository extends BaseJongoRepository<JobNotification, String> {

    @Autowired
    private JobRepository jobRepository;

    protected JobNotificationRepository() {
        super(JobNotification.class);
    }

    @Override
    protected String getCollectionName() {
        return TvsCollections.JOB_NOTIFICATION.getName();
    }

    public JobNotification findByJobId(String jobId) {
        return findOneBy("{jobId: #}", jobId);
    }
}

package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.verdict.AllureReportLog;
import org.springframework.stereotype.Repository;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;

@Repository
public class AllureReportLogRepository extends BaseJongoRepository<AllureReportLog, String> {

    protected AllureReportLogRepository() {
        super(AllureReportLog.class);
    }

    @Override
    protected String getCollectionName() {
        return ALLURE_REPORT_LOG.getName();
    }

    public AllureReportLog findByJobExecutionId(String jobExecutionId) {
        return getCollection()
            .findOne("{jobExecutionId: #}", jobExecutionId)
            .as(AllureReportLog.class);
    }
}

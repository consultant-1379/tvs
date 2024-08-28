package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.AllureReportLog;
import com.ericsson.gic.tms.tvs.domain.repositories.AllureReportLogRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.AllureReportLogBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.*;

@Service
public class AllureReportLogService {

    @Autowired
    private AllureReportLogRepository allureReportLogRepo;

    @Autowired
    private MapperFacade mapperFacade;

    public AllureReportLogBean addLog(AllureReportLogBean logBean) {
        String jobExecutionId = logBean.getJobExecutionId();
        Optional<AllureReportLog> log = Optional.ofNullable(allureReportLogRepo.findByJobExecutionId(jobExecutionId));
        if (!log.isPresent()) {
            AllureReportLog allureReportLog = mapperFacade.map(logBean, AllureReportLog.class);
            log = Optional.of(allureReportLogRepo.save(allureReportLog));
        }
        return mapperFacade.map(log.get(), AllureReportLogBean.class);
    }

    public AllureReportLogBean findByJobExecutionId(String jobExecutionId) {
        AllureReportLog log = verifyFound(allureReportLogRepo.findByJobExecutionId(jobExecutionId));
        return mapperFacade.map(log, AllureReportLogBean.class);
    }
}

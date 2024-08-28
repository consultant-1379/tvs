package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.application.services.AllureReportLogService;
import com.ericsson.gic.tms.tvs.presentation.dto.AllureReportLogBean;
import com.ericsson.gic.tms.tvs.presentation.resources.AllureReportLogResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AllureReportLogController extends AbstractJsonApiCapableController implements AllureReportLogResource {

    @Autowired
    private AllureReportLogService allureReportLogService;

    @Override
    public Document<AllureReportLogBean> addLog(AllureReportLogBean bean) {
        return responseFor(allureReportLogService.addLog(bean))
            .withSelfRel(AllureReportLogResource.class)
            .build();
    }

    @Override
    public Document<AllureReportLogBean> find(String jobExecutionId) {
        return responseFor(allureReportLogService.findByJobExecutionId(jobExecutionId))
            .withSelfRel(AllureReportLogResource.class)
            .build();
    }
}

package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.application.services.JobConfigurationService;
import com.ericsson.gic.tms.tvs.presentation.dto.JobConfigurationBean;
import com.ericsson.gic.tms.tvs.presentation.resources.JobConfigurationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class JobConfigurationController extends AbstractJsonApiCapableController implements JobConfigurationResource {

    @Autowired
    private JobConfigurationService jobConfigurationService;

    @Override
    public Document<JobConfigurationBean> get(String jobName, String source) {
        return responseFor(jobConfigurationService.getContext(jobName, source))
            .withSelfRel(JobConfigurationResource.class, JOB_NAME)
            .build();
    }
}

package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.application.services.JobService;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.resources.JobInsertResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class JobInsertController extends AbstractJsonApiCapableController implements JobInsertResource {

    @Autowired
    private JobService jobService;

    @Override
    public Document<JobBean> updateJob(String contextId, String jobName, JobBean bean) {
        return responseFor(jobService.updateJob(contextId, jobName, bean))
            .withSelfRel(JobInsertResource.class)
            .build();
    }
}

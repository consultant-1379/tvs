package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.application.services.JobService;
import com.ericsson.gic.tms.tvs.application.services.ResultPath;
import com.ericsson.gic.tms.tvs.application.services.TestSessionService;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.resources.TestSessionInsertResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TestSessionInsertController extends AbstractJsonApiCapableController implements TestSessionInsertResource {

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobService jobService;

    @Override
    public Document<TestSessionBean> updateTestSession(String contextId, String jobName, String executionId,
                                                       TestSessionBean bean) {
        Job job = jobRepository.findByNameAndContextId(jobName, contextId);

        if (job == null) {
            job = new Job();
            job.setContextId(contextId);
            job.setName(jobName);

            job = jobRepository.save(job);
        }

        ResultPath resultPath = new ResultPath()
            .withJob(contextId, job.getUid());

        TestSessionBean resource = testSessionService.updateTestSession(resultPath, executionId, bean);
        jobService.aggregateJob(job.getUid());

        return responseFor(resource)
            .withSelfRel(TestSessionInsertResource.class)
            .build();
    }
}

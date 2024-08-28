package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/contexts/{contextId}/jobs/{jobName}/test-sessions/{executionId}"
    + "/test-suite-results/{suiteName}/test-case-results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TestCaseResultInsertResource {

    /**
     * <p>Creates or updates test case result by the given flow of required parameters</p>
     *
     * @param contextId required ID of context
     * @param jobName required name of job
     * @param executionId required ID of job execution
     * @param suiteName required name of suite name
     * @param testCaseResultId required named ID of test case result
     * @param testCaseResultBean payload of test case result data
     * @return
     *      created or updated test case result entity
     */
    @POST
    @Path("{testCaseResultId}")
    @TypeHint(TestCaseResultBean.class)
    Document<TestCaseResultBean> updateTestCaseResult(@PathParam("contextId") String contextId,
                                                      @PathParam("jobName") String jobName,
                                                      @PathParam("executionId") String executionId,
                                                      @PathParam("suiteName") String suiteName,
                                                      @PathParam("testCaseResultId") String testCaseResultId,
                                                      @NotNull @Valid TestCaseResultBean testCaseResultBean);

}

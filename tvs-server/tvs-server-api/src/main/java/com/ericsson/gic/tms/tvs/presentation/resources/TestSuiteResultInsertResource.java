package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/contexts/{contextId}/jobs/{jobName}/test-sessions/{executionId}/test-suite-results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TestSuiteResultInsertResource {

    /**
     * <p>Creates or updates test suite by the given chain of required parameters
     * to put under the certain context of job, job execution and context</p>
     *
     * @param contextId required ID of context
     * @param jobName required name of job
     * @param executionId required ID of job execution
     * @param testSuiteName required name of test suite
     * @param testSuiteResultBean required payload of test suite data
     * @return
     *      created or updated test suite entity bean
     */
    @POST
    @Path("{testSuiteName}")
    @TypeHint(TestSuiteResultBean.class)
    Document<TestSuiteResultBean> updateTestSuiteResult(@PathParam("contextId") String contextId,
                                                        @PathParam("jobName") String jobName,
                                                        @PathParam("executionId") String executionId,
                                                        @PathParam("testSuiteName") String testSuiteName,
                                                        @NotNull @Valid TestSuiteResultBean testSuiteResultBean);

}

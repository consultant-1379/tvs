package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/contexts/{contextId}/jobs/{jobName}/test-sessions/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TestSessionInsertResource {

    /**
     * <p>Creates or updates job execution session by the given parameters</p>
     *
     * @param contextId required ID of context
     * @param jobName required name of job
     * @param executionId the ID of job execution session
     * @param jobExecutionBean required data of job execution session
     *
     * @return
     *      created or updated test session entity bean
     */
    @POST
    @Path("{executionId}")
    @TypeHint(TestSessionBean.class)
    Document<TestSessionBean> updateTestSession(@PathParam("contextId") String contextId,
                                                @PathParam("jobName") String jobName,
                                                @PathParam("executionId") String executionId,
                                                @NotNull @Valid TestSessionBean jobExecutionBean);
}

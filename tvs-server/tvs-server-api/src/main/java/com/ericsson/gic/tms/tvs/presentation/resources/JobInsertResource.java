package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/contexts/{contextId}/jobs/{jobName}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface JobInsertResource {

    /**
     * <p>Creates or updates job and its related job executions (sessions) by the given context ID and job name.</p>
     *
     * @param contextId required ID of context
     * @param jobName required name of job
     * @param jobDetails payload of job attribute values
     * @return
     *      created or updated job entity
     */
    @POST
    @TypeHint(JobBean.class)
    Document<JobBean> updateJob(@PathParam("contextId") String contextId,
                                @PathParam("jobName") String jobName,
                                @NotNull @Valid JobBean jobDetails);
}

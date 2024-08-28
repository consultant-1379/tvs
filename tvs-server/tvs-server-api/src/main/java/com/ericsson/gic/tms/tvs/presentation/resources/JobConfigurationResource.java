package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.presentation.dto.JobConfigurationBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/jobs/{jobName}/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface JobConfigurationResource {

    String SOURCE = "source";
    String JOB_NAME = "jobName";

    /**
     * <p>Retrieve job configuration details of mapping job to the certain context</p>
     *
     * @param jobName the given name of the job
     * @param source the given source
     * @return
     *      single job configuration details
     */
    @GET
    @TypeHint(JobConfigurationBean.class)
    Document<JobConfigurationBean> get(@PathParam(JOB_NAME) String jobName,
                                       @QueryParam(SOURCE) String source);
}

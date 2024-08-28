package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.JobReport;
import com.ericsson.gic.tms.tvs.presentation.dto.JobStatisticsBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static com.ericsson.gic.tms.presentation.dto.common.Paging.*;
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.ORDER_BY_PARAM;
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.ORDER_MODE_PARAM;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface JobResource {

    String JOB_ID = "{jobId}";
    String CONTEXT_ID = "contextId";
    String EXPORT_CSV = "/csv";
    String STATISTICS = JOB_ID + "/statistics";
    String AGGREGATE_JOB = JOB_ID + "/aggregate";

    /**
     * <p>Retrieves list of jobs by context ID and other filtration parameters</p>
     *
     * @param contextId optional ID of context
     * @param page the number of page of data items
     * @param size the maximum number of row item of drops report data
     * @param orderBy specify a field to sort by. By default 'id' field.
     * @param orderMode sorting mode received collection.
     *                  See also {@link com.ericsson.gic.tms.presentation.dto.common.SortingMode}
     * @param query string of query parameters to filter request list
     * @return
     *      collection of jobs
     */
    @GET
    @TypeHint(JobBean.class)
    DocumentList<JobBean> getList(@QueryParam(CONTEXT_ID) String contextId,
                                  @QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                  @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1) int size,
                                  @QueryParam(ORDER_BY_PARAM) @DefaultValue("id") String orderBy,
                                  @QueryParam(ORDER_MODE_PARAM) @DefaultValue("DESC") SortingMode orderMode,
                                  @QueryParam("q") String query);

    /**
     * <p>Retrieves all jobs by the given context ID if specified</p>
     *
     * @param contextId optional ID of context
     * @return
     *      list of exported jobs by the given parameter
     */
    @GET
    @Path(EXPORT_CSV)
    @TypeHint(JobReport.class)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<JobReport> exportJobs(@QueryParam(CONTEXT_ID) String contextId);

    /**
     * <p>Retrieves job by the given ID</p>
     * @param jobId the ID of job
     * @return
     *      job entity
     */
    @GET
    @Path(JOB_ID)
    @TypeHint(JobBean.class)
    Document<JobBean> getJob(@PathParam("jobId") String jobId);

    /**
     * <p>Retrieve job execution statistics by job ID</p>
     *
     * @param jobId required ID of job
     * @param query
     * @param orderBy Default is Date. Other options are Drop and Iso Version
     *
     * @return
     */
    @GET
    @Path(STATISTICS)
    @TypeHint(JobStatisticsBean.class)
    DocumentList<JobStatisticsBean> getStatistics(@PathParam("jobId") String jobId,
                                                  @QueryParam("q") String query,
                                                  @QueryParam("limit") Integer limit,
                                                  @QueryParam(ORDER_BY_PARAM) @DefaultValue("Date") String orderBy);

    /**
     * <p>Perform aggregation of job data by the given job ID and return aggregated result</p>
     *
     * @param jobId required ID of job
     * @return
     *      aggregated job data
     */
    @GET
    @Path(AGGREGATE_JOB)
    @TypeHint(JobBean.class)
    Document<JobBean> aggregateJob(@PathParam("jobId") String jobId);
}

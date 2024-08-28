package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.JobExecutionReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TraversalMode;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;

import static com.ericsson.gic.tms.presentation.dto.common.Paging.*;
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.ORDER_BY_PARAM;
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.ORDER_MODE_PARAM;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/jobs/{jobId}/test-sessions")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface TestSessionResource {

    String EXPORT_CSV = "/csv";
    String TEST_SESSION = "{executionId}";
    String TEST_SESSION_AGGREGATION = TEST_SESSION + "/aggregate";
    String TEST_CASE_RESULTS = TEST_SESSION + "/test-case-results";

    /**
     * <p>Retrieves the list of job sessions</p>
     *
     * @param jobId required ID of job
     * @param startTime start time of test case results
     * @param endTime   end time of test case results
     * @param page the number of page of data items
     * @param size the maximum number of row item of drops report data
     * @param orderBy specify a field to sort by. By default 'time.startDate' field.
     * @param orderMode sorting mode for received collection. By default 'DESC'.
     *                  See also {@link com.ericsson.gic.tms.presentation.dto.common.SortingMode}
     * @param query string of query parameters to filter request list
     * @return
     *  the list of job sessions
     */
    @GET
    @TypeHint(TestSessionBean.class)
    DocumentList<TestSessionBean> getList(@PathParam("jobId") String jobId,
                                          @QueryParam("startTime") LocalDateTime startTime,
                                          @QueryParam("endTime") LocalDateTime endTime,
                                          @QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                          @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1) int size,
                                          @QueryParam(ORDER_BY_PARAM) @DefaultValue("time.startDate") String orderBy,
                                          @QueryParam(ORDER_MODE_PARAM) @DefaultValue("DESC") SortingMode orderMode,
                                          @QueryParam("q") String query);

    /**
     * <p>Export all job executions by the given required job ID</p>
     * @param jobId required ID of job
     * @return
     *      all job executions by job ID
     */
    @GET
    @Path(EXPORT_CSV)
    @TypeHint(JobExecutionReport.class)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<JobExecutionReport> getList(@PathParam("jobId") String jobId);

    /**
     * <p>Retrieves the certain job execution details by the given parameters</p>
     * @param jobId required ID of job
     * @param executionId required ID of job execution
     * @return
     *      job session bean entity
     */
    @GET
    @Path(TEST_SESSION)
    @TypeHint(TestSessionBean.class)
    Document<TestSessionBean> getTestSession(@PathParam("jobId") String jobId,
                                             @PathParam("executionId") String executionId);

    /**
     * <p>Perform aggregation of job execution and returns aggregated result</p>
     *
     * @param jobId required ID of job
     * @param executionId required ID of job execution
     * @param mode aggregation depth
     *                  'SELF' is used to aggregate given execution only (default)
     *                  'ASCENDING" is used to aggregate given execution and then parent job
     *                  'WIDE" is used to aggregate child test suites, then given execution and then parent job
     * @return
     *      job execution aggregated result
     *
     * @deprecated aggregation is performed implicitly
     * on test session update {@link TestSessionInsertResource#updateTestSession}
     */
    @GET
    @Deprecated
    @Path(TEST_SESSION_AGGREGATION)
    @TypeHint(TestSessionBean.class)
    Document<TestSessionBean> aggregateTestSession(@PathParam("jobId") String jobId,
                                                   @PathParam("executionId") String executionId,
                                                   @QueryParam("mode") @DefaultValue("SELF") TraversalMode mode);

    /**
     * <p>Retrieve all test case results by job execution ID</p>
     *
     * @param jobId job ID
     * @param executionId job execution ID
     *
     * @return
     *      test case results
     */
    @GET
    @Path(TEST_CASE_RESULTS)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<TestCaseResultReport> exportTestCaseResults(@PathParam("jobId") String jobId,
                                                             @PathParam("executionId") String executionId);
}

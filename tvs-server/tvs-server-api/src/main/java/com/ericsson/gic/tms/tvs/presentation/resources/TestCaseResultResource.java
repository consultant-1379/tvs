package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultReport;
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

@Path("/jobs/{jobId}/test-sessions/{testSessionExecutionId}/test-suite-results/{testSuiteName}/test-case-results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TestCaseResultResource {

    String EXPORT_CSV = "/csv";

    /**
     * <p>Retrieve a collection of test case results by the certain chain of required parameters and filtration
     * parameters</p>
     *
     * @param jobId required ID of job
     * @param jobExecutionId required ID of job execution
     * @param testSuiteName required name of test suite
     * @param page the number of page of data items
     * @param size the maximum number of row item of drops report data
     * @param orderBy specify a field to sort by. By default 'time.startDate' field.
     * @param orderMode sorting mode received collection.
     *                  See also {@link com.ericsson.gic.tms.presentation.dto.common.SortingMode}
     * @param query string of query parameters to filter request list
     * @return
     *      collection of test case results
     */
    @GET
    @TypeHint(TestCaseResultBean.class)
    DocumentList<TestCaseResultBean> getList(@PathParam("jobId") String jobId,
                                             @PathParam("testSessionExecutionId") String jobExecutionId,
                                             @PathParam("testSuiteName") String testSuiteName,
                                             @QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                             @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1) int size,
                                             @QueryParam(ORDER_BY_PARAM) @DefaultValue("time.startDate") String orderBy,
                                             @QueryParam(ORDER_MODE_PARAM) @DefaultValue("DESC") SortingMode orderMode,
                                             @QueryParam("q") String query);

    /**
     * <p>Exports all test case results by the given chain of required parameters.</p>
     *
     * @param jobId required ID of job
     * @param jobExecutionId required ID of job execution
     * @param testSuiteName required name of test suite
     * @return
     *      collection of test case result report
     */
    @GET
    @Path(EXPORT_CSV)
    @TypeHint(TestCaseResultReport.class)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<TestCaseResultReport> getList(@PathParam("jobId") String jobId,
                                               @PathParam("testSessionExecutionId") String jobExecutionId,
                                               @PathParam("testSuiteName") String testSuiteName);

    /**
     * <p>Retrieves test case result by the given required parameters</p>
     *
     * @param jobId required ID of job
     * @param jobExecutionId required ID of job execution
     * @param testSuiteName required name of test suite
     * @param testCaseResultId
     * @return
     *      entity of test case result
     */
    @GET
    @Path("{testCaseResultId}")
    @TypeHint(TestCaseResultBean.class)
    Document<TestCaseResultBean> getTestCaseResult(@PathParam("jobId") String jobId,
                                                   @PathParam("testSessionExecutionId") String jobExecutionId,
                                                   @PathParam("testSuiteName") String testSuiteName,
                                                   @PathParam("testCaseResultId") String testCaseResultId);
}

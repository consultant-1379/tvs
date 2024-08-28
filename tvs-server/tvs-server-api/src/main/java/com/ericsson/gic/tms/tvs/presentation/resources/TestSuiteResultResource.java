package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
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
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.*;

@Path("/jobs/{jobId}/test-sessions/{executionId}/test-suite-results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TestSuiteResultResource {

    String TEST_SUITE = "{testSuiteName}";
    String TEST_SUITE_AGGREGATION = TEST_SUITE + "/aggregate";

    /**
     * <p>Retrieves the list of test suites by the given list of filtration parameters</p>
     *
     * @param jobId required ID of job
     * @param executionId required ID of job execution
     * @param page the number of page of data items
     * @param size the maximum number of row item of drops report data
     * @param orderBy specify a field to sort by. By default 'time.startDate' field.
     * @param orderMode sorting mode for received collection. By default 'DESC'.
     *                  See also {@link com.ericsson.gic.tms.presentation.dto.common.SortingMode}
     * @param query string of query parameters to filter request list
     * @return
     *      collection of test suites
     */
    @GET
    @TypeHint(TestSuiteResultBean.class)
    DocumentList<TestSuiteResultBean> getList(@PathParam("jobId") String jobId,
                                              @PathParam("executionId") String executionId,
                                              @QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                              @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1) int size,
                                              @QueryParam(ORDER_BY_PARAM) @DefaultValue("time.startDate")
                                              String orderBy,
                                              @QueryParam(ORDER_MODE_PARAM) @DefaultValue("DESC")
                                              SortingMode orderMode,
                                              @QueryParam("q") String query);

    /**
     * <p>Retrieves the certain test suite by the given parameters</p>
     *
     * @param jobId required ID of job
     * @param executionId required ID of job execution
     * @param testSuiteName required name of test suite
     * @return
     *      test suite enitity bean
     */
    @GET
    @Path(TEST_SUITE)
    @TypeHint(TestSuiteResultBean.class)
    Document<TestSuiteResultBean> getTestSuiteResult(@PathParam("jobId") String jobId,
                                                     @PathParam("executionId") String executionId,
                                                     @PathParam("testSuiteName") String testSuiteName);

    /**
     * <p>Performs test suite data aggregation and returns its results</p>
     *
     * @param jobId required ID of job
     * @param executionId required ID of job execution
     * @param testSuiteName required name of test suite
     * @return
     *      aggregation result of test suites
     */
    @GET
    @Path(TEST_SUITE_AGGREGATION)
    @TypeHint(TestSuiteResultBean.class)
    Document<TestSuiteResultBean> aggregateTestSuiteResult(@PathParam("jobId") String jobId,
                                                           @PathParam("executionId") String executionId,
                                                           @PathParam("testSuiteName") String testSuiteName);

}

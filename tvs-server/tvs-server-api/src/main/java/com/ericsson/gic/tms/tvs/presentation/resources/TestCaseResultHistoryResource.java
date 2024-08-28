package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseImportStatus;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultHistoryBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;

import static com.ericsson.gic.tms.presentation.dto.common.Paging.*;
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.ORDER_BY_PARAM;
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.ORDER_MODE_PARAM;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/test-case-results")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface TestCaseResultHistoryResource {

    String EXECUTION_ID = "{executionId}";
    String TEST_CASE_HISTORY = "{testCaseId}/history";
    String TEST_CASE_HISTORY_CSV = "{testCaseId}/history/csv";

    /**
     * <p>Retrieves paginated collection of all execution results of Test Cases by the given parameter criteria</p>
     *
     * @param importStatus Optional. The Import status of Test Case result to be retrieved.
     * @param page number of page
     * @param size the limit the number of items per page
     *
     * @return
     *      all execution results of Test Cases
     */
    @GET
    @TypeHint(TestCaseResultBean.class)
    DocumentList<TestCaseResultBean> getList(@QueryParam("importStatus") TestCaseImportStatus importStatus,
                                             @QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                             @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1) int size);

    /**
     * <p>Retrieves a paginated collection of Test Case Execution History for all Test Sessions
     * that were executed for this test case. If a specific Test Activity Id is provided, then
     * only execution results for that Test Session will be returned</p>
     *
     * @param testCaseId named ID of Test Case, e.g. "TAF_KVM_Func_1"
     * @param startTime start of execution date range (Optional field)
     * @param stopTime end of execution date range (Optional field)
     * @param page       number of page
     * @param size       the limit the number of items per page
     * @param orderBy    specify a field to sort by
     * @param orderMode  sort based on order of results. Options are listed
     *                   in {@link com.ericsson.gic.tms.presentation.dto.common.SortingMode}.
     *                   A "DESC" is used for descending order (default).
     * @return paginated list of records and total count of records
     */
    @GET
    @Path(TEST_CASE_HISTORY)
    @TypeHint(TestCaseResultHistoryBean.class)
    DocumentList<TestCaseResultHistoryBean> getList(@PathParam("testCaseId") String testCaseId,
                                                    @QueryParam("startTime") LocalDateTime startTime,
                                                    @QueryParam("stopTime") LocalDateTime stopTime,
                                                    @QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                                    @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1) int size,
                                                    @QueryParam(ORDER_BY_PARAM) @DefaultValue("startDate")
                                                    String orderBy,
                                                    @QueryParam(ORDER_MODE_PARAM) @DefaultValue("DESC")
                                                    SortingMode orderMode,
                                                    @QueryParam("q") String query);

    /**
     * <p>Exports collection of test case results by the certain test case</p>
     *
     * @param testCaseId required ID of test case
     * @return
     *      collection of test case results by the certain test case
     */
    @GET
    @Path(TEST_CASE_HISTORY_CSV)
    @TypeHint(TestCaseReport.class)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<TestCaseReport> getList(@PathParam("testCaseId") String testCaseId);

    /**
     * <p>Updates some of provided Test Case result fields by the given execution ID of test case result document.</p>
     *
     * @param executionId unique execution ID of test case result document
     * @param testCaseResultBean test case payload to be updated
     *
     * @return
     *      update test case result bean
     */
    @PUT
    @Path(EXECUTION_ID)
    Document<TestCaseResultBean> update(@PathParam("executionId") @NotNull String executionId,
                                        @NotNull @Valid TestCaseResultBean testCaseResultBean);
}

package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseBean;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseReport;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;

import static com.ericsson.gic.tms.presentation.dto.common.Paging.SIZE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/jobs")
public interface TestCaseFlakinessResource {

    String JOB_ID = "{jobId}";
    String DEFAULT_REPORT_SIZE = "100";
    String FLAKINESS = "/test-flakiness";
    String CSV = "/csv";
    String TEST_CASE = "/testcase/{testCaseId}/test-flakiness";

    /**
     * Returns a list of top flaky test cases in a given job,
     * sorted from most flaky to least flaky
     * <p>
     * Meta:
     * <ul><li>totalItems - total test cases for this query</li></ul>
     *
     * @param jobId     the ID of the subject job
     * @param startTime start time of test case results
     * @param endTime   end time of test case results
     * @param size      amount of flaky test cases in the top
     * @return
     */
    @GET
    @Path(JOB_ID + FLAKINESS)
    @TypeHint(FlakyTestCaseBean.class)
    DocumentList<FlakyTestCaseBean> getAllTestCaseFlakinessByJob(
        @PathParam("jobId") String jobId,
        @QueryParam("startTime") @NotNull LocalDateTime startTime,
        @QueryParam("endTime") @NotNull LocalDateTime endTime,
        @QueryParam(SIZE) @DefaultValue(DEFAULT_REPORT_SIZE) @Min(1) int size);

    /**
     * Returns a flaky test case in a given job and testcase,
     * <p>
     * Meta:
     * <ul><li>totalItems - total test cases for this query</li></ul>
     *
     * @param jobId     the ID of the subject job
     * @param testCaseId     the ID of the subject testcase
     * @param startTime start time of test case results
     * @param endTime   end time of test case results
     * @return
     */
    @GET
    @Path(JOB_ID + TEST_CASE)
    @TypeHint(FlakyTestCaseBean.class)
    Document<FlakyTestCaseBean> getSingleTestCaseFlakiness(
        @PathParam("jobId") String jobId,
        @PathParam("testCaseId") String testCaseId,
        @QueryParam("startTime") @NotNull LocalDateTime startTime,
        @QueryParam("endTime") @NotNull LocalDateTime endTime);


    @GET
    @Path(JOB_ID + FLAKINESS + CSV)
    @TypeHint(FlakyTestCaseReport.class)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<FlakyTestCaseReport> exportTestCaseFlakinessReportByJob(
        @PathParam("jobId") String jobId,
        @QueryParam("startTime") @NotNull LocalDateTime startTime,
        @QueryParam("endTime") @NotNull LocalDateTime endTime,
        @QueryParam(SIZE) @DefaultValue(DEFAULT_REPORT_SIZE) @Min(1) int size);
}

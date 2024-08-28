package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.GenericReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoCsvReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.ReportType;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.TestCaseIsoTrendReport;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/contexts/{contextId}/reports")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface TestCaseReportResource {

    String ISO_CSV_REPORT = "/iso/csv";
    String TREND_CSV_REPORT = "/trend/csv";

    /**
     * Data format this endpoint produces depends on report and reportType query parameters
     *
     * @param contextId context to use data from
     * @param reportType combination of iso/trend and priority/group/component/suite
     *                   e.g.
     *                   iso-priority
     *                   see {@link ReportType} for more examples
     * @param iso specific iso version (for ISO report) or start iso version (for trend report)
     * @param toIso end iso version (for trend report)
     * @param topIsoCount number of last iso versions (for trend report)
     * @param query string of query parameters to filter trend report
     *              keys:
     *              groupBy - priority/group/component/suite name
     *              jobId - job in given context to use data from
     *              resultCode - return priority/group/component/suite having test cases with specified result codes
     *                           in given ISO range
     *
     * @return
     */
    @GET
    @TypeHint(GenericReportBean.class)
    DocumentList<?> getReport(@PathParam("contextId") String contextId,
                              @QueryParam("reportType") @NotNull ReportType reportType,
                              @QueryParam("iso") String iso,
                              @QueryParam("toIso") String toIso,
                              @QueryParam("topIsoCount") int topIsoCount,
                              @QueryParam("jobNames") String jobIds,
                              @QueryParam("q") String query);


    @GET
    @Path(ISO_CSV_REPORT)
    @TypeHint(IsoReportBean.class)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<IsoCsvReport> exportIsoReports(@PathParam("contextId") String contextId,
                                                @QueryParam("jobNames") String jobNames,
                                                @QueryParam("reportType") @NotNull ReportType reportType,
                                                @QueryParam("iso") String isoVersion);

    @GET
    @Path(TREND_CSV_REPORT)
    @TypeHint(TestCaseIsoTrendReport.class)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<TestCaseIsoTrendReport> exportTrendReports(@PathParam("contextId") String contextId,
                                                            @QueryParam("jobNames") String jobNames,
                                                            @QueryParam("reportType") @NotNull ReportType reportType,
                                                            @QueryParam("iso") String iso,
                                                            @QueryParam("toIso") String toIso);

}

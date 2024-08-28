package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportType;
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
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.*;
import static javax.ws.rs.core.MediaType.*;

@Path("/jobs/{jobId}/reports")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface JobReportResource {

    /**
     * <p>Retrieves collection of items of the certain report under certain context and filters them
     * by passed parameters.</p>
     *
     * @param jobId     required unique system ID of the job
     * @param reportId  the ID of the report. Available the following IDs:
     *                  <ul>
     *                  <p>
     *                  <li><code>drop-report-table</code> a report collects drop items each of which has
     *                  <strong>aggregated</strong> number of <strong>unique test cases</strong>
     *                  (count by execution ID),test suites, finished job sessions and
     *                  test cases pass rate in percentage.</li>
     *                  <p>
     *                  <li><code>drop-report-chart</code> a report collects the following field data
     *                  for chart visualization:
     *                  <ul>
     *                  <li>drop name</li>
     *                  <li>last ISO version of the drop</li>
     *                  <li>number of test case executions per last ISO version</li>
     *                  <li>test cases pass rate per drop</li>
     *                  </ul>
     *                  </li>
     *                  <p>
     *                  </ul>
     * @param since     the start of the date range over which you want to search. The since parameter is optional.
     *                  By default, a date starting a month ago.
     * @param until     the end of the date range over which you want to search. This should be in the same format
     *                  as <code>since</code> and less then <code>since</code>.
     *                  By default, current data time of the server.
     * @param page      the number of page of data items
     * @param size      the maximum number of row item of drops report data
     * @param orderBy   specify a field to sort by. By default drop version.
     * @param orderMode the name of sort mode to sort drops report data items on.
     *                  A "ASC" is used for descending order (default).
     * @param query     query string to filter requested data.
     * @param reportType    Type of report, either "Last ISO" or "Cumulative"
     * @return paginated collection of items of the certain report under certain context and filtered
     * by passed parameters
     */
    @GET
    @TypeHint(DropReportBean.class)
    DocumentList<? extends DropReportBean> getData(@PathParam("jobId") String jobId,
                                                   @QueryParam("id") String reportId,
                                                   @QueryParam("since") LocalDateTime since,
                                                   @QueryParam("until") LocalDateTime until,
                                                   @QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                                   @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1)
                                                   int size,
                                                   @QueryParam(ORDER_BY_PARAM) @DefaultValue("dropName")
                                                   String orderBy,
                                                   @QueryParam(ORDER_MODE_PARAM) @DefaultValue("ASC")
                                                   SortingMode orderMode,
                                                   @QueryParam("q") @DefaultValue("") String query,
                                                   @QueryParam("type") @DefaultValue("LAST_ISO")
                                                   DropReportType reportType);
}

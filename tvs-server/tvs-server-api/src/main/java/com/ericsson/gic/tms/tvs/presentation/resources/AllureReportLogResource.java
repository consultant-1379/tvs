package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.presentation.dto.AllureReportLogBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static javax.ws.rs.core.MediaType.*;

@Path("/allure-report-logs")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface AllureReportLogResource {

    /**
     * <p>Creates/adds a new allure report log entry.</p>
     *
     * @param bean allure report log bean
     * @return
     *      saved allure report log bean
     */
    @POST
    @TypeHint(AllureReportLogBean.class)
    Document<AllureReportLogBean> addLog(@NotNull @Valid AllureReportLogBean bean);

    /**
     * <p>Retrieves allure report log entry by <strong>job execution id</strong></p>
     *
     * @param jobExecutionId job execution ID
     * @return
     *      found allure report log
     */
    @GET
    @TypeHint(AllureReportLogBean.class)
    Document<AllureReportLogBean> find(@QueryParam("jobExecutionId") String jobExecutionId);
}

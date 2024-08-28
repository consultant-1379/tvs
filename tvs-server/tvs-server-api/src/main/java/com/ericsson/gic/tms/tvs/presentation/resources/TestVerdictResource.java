package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseIdBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestVerdictBean;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;

@Path("test-results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TestVerdictResource {

    String TEST_CASE_ID = "{testCaseId}";

    /**
     * <p>Retrieved list of test case identifiers by the given optional test case create date parameter.</p>
     *
     * @param dateFrom optional. Created data of test case result.
     * @return
     *      list of found test case identifiers
     */
    @GET
    DocumentList<TestCaseIdBean> getTestResultIds(@QueryParam("dateFrom") LocalDateTime dateFrom);

    /**
     * <p>Retrieves the certain test case by the given test case ID</p>
     *
     * @param testCaseId required ID of test case result
     * @return
     *      test case result
     */
    @GET
    @Path(TEST_CASE_ID)
    Document<TestVerdictBean> getTestResult(@PathParam("testCaseId") String testCaseId);

}

package com.ericsson.gic.tms.tvs.presentation.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by egergle on 19/10/2017.
 */

@Path("/metrics")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface StatisticsResource {

    /**
     * <p>Retrieves user statistics from server</p>
     *
     * @return
     *       collection of statistics
     */
    @GET
    @Path("/users")
    Response getUsers();

    /**
     * <p>Retrieves url hit count for previous 6 months statistics from server</p>
     *
     * @return
     *       collection of statistics
     */
    @GET
    @Path("/url")
    Response getUrlHitCount();
}

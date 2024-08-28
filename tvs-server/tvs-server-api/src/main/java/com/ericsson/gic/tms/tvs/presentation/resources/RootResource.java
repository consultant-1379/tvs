package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.References;
import com.ericsson.gic.tms.presentation.dto.bean.StatisticsBean;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.tvs.presentation.dto.AggregationInfoBean;
import com.ericsson.gic.tms.tvs.presentation.dto.StatusListBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RootResource {

    String REFERENCES_PATH = "references";

    /**
     * Returns all enum constants grouped together
     *
     * @return References
     */
    @GET
    @Path(REFERENCES_PATH)
    @TypeHint(References.class)
    Document<References> getReferences();

    /**
     * @return status of TVS system, e.g. UP, DOWN, etc.
     */
    @GET
    @Path("health")
    String getHealth();

    /**
     * @return environment variables of host system
     */
    @GET
    @Path("env")
    Map<String, Object> getEnvironment();

    /**
     * @return client requests statistics of TVS system
     */
    @GET
    @Path("statistics")
    StatisticsBean getStatistics();

    /**
     * @return the list of all available classifiers
     */
    @GET
    @Path("statuses")
    StatusListBean getStatuses();

    /**
     * <p>Retrieve a status of actual triggered process of common data aggregation</p>
     *
     * @return
     *      status of actual aggregation process
     */
    @GET
    @Path("aggregate")
    AggregationInfoBean getAggregationInfo();

    /**
     * <p>Asynchronously triggers job and its related documents data aggregation</p>
     *
     * @return
     *  status of trigger action of job aggregation
     */
    @POST
    @Path("aggregate")
    AggregationInfoBean aggregate();
}

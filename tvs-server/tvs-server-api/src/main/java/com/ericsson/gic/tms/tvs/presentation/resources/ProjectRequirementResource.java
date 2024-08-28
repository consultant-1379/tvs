package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.ProjectRequirementBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementDetailsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementReport;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import static com.ericsson.gic.tms.presentation.dto.common.Paging.*;
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.ORDER_BY_PARAM;
import static com.ericsson.gic.tms.presentation.dto.common.Sorting.ORDER_MODE_PARAM;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/project-requirements")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface ProjectRequirementResource {

    String EXPORT_CSV = "/csv";
    String REQUIREMENT_ID = "{requirementId}";
    String REQUIREMENT_CHILDREN = REQUIREMENT_ID + "/children";
    String REQUIREMENT_CHILDREN_CSV = REQUIREMENT_ID + "/children/csv";

    /**
     * <p>Save or updates existing project requirement hierarchy.</p>
     *
     * @param bean a payload of project requirement data
     *
     * @return
     *      saved requirement bean
     */
    @POST
    @TypeHint(ProjectRequirementBean.class)
    Document<ProjectRequirementBean> save(@NotNull @Valid ProjectRequirementBean bean);

    /**
     * <p>Retrieves all paginated collection of requirements of all project by given filter parameters</p>
     *
     * @param page number of page
     * @param size the limit the number of items per page
     * @param orderBy specify a field to sort by
     * @param orderMode sort based on order of results. Options are listed
     * @param query sort based on order of results. Options are listed
     *              in {@link com.ericsson.gic.tms.presentation.dto.common.SortingMode}.
     * @return
     *      paginated collection of requirements
     */
    @GET
    @TypeHint(RequirementDetailsBean.class)
    DocumentList<RequirementDetailsBean> getRequirements(@QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                                         @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1) int size,
                                                         @QueryParam(ORDER_BY_PARAM) @DefaultValue("id") String orderBy,
                                                         @QueryParam(ORDER_MODE_PARAM) @DefaultValue("DESC")
                                                         SortingMode orderMode,
                                                         @QueryParam("q") String query);

    /**
     * <p>Export all project requirements</p>
     *
     * @return
     *      project requirements
     */
    @GET
    @Path(EXPORT_CSV)
    @TypeHint(RequirementReport.class)
    @Produces({"text/csv", APPLICATION_JSON})
    DocumentList<RequirementReport> getRequirements();

    /**
     * <p>Retrieves paginated collection of requirements which belong to the given requirement ID and by
     * given filter parameters</p>
     *
     * @param requirementId required unique ID of issue tracker requirement
     * @param page number of page
     * @param size the limit the number of items per page
     * @param orderBy specify a field to sort by
     * @param orderMode sort based on order of results. Options are listed
     *                  in {@link com.ericsson.gic.tms.presentation.dto.common.SortingMode}.
     * @param query query for filtering
     * @return
     *      paginated collection of requirements
     */
    @GET
    @Path(REQUIREMENT_CHILDREN)
    @TypeHint(RequirementDetailsBean.class)
    DocumentList<RequirementDetailsBean> getRequirements(@PathParam("requirementId") String requirementId,
                                                         @QueryParam(PAGE) @DefaultValue("1") @Min(1) int page,
                                                         @QueryParam(SIZE) @DefaultValue(DEFAULT_SIZE) @Min(1) int size,
                                                         @QueryParam(ORDER_BY_PARAM) @DefaultValue("id") String orderBy,
                                                         @QueryParam(ORDER_MODE_PARAM) @DefaultValue("DESC")
                                                            SortingMode orderMode,
                                                         @QueryParam("q") String query);

    /**
     * <p>Exports requirement children elements</p>
     *
     * @param requirementId required ID of parent requirement ID
     *
     * @return
     *  collection of requirement's children
     */
    @GET
    @Path(REQUIREMENT_CHILDREN_CSV)
    @Produces({"text/csv", APPLICATION_JSON})
    @TypeHint(RequirementReport.class)
    DocumentList<RequirementReport> getRequirements(@PathParam("requirementId") String requirementId);

    /**
     * <p>Retrieve data of requirement by its unique ID</p>
     *
     * @param requirementId required unique ID of issue tracker requirement
     * @return
     *      data of single requirement
     */
    @GET
    @Path(REQUIREMENT_ID)
    @TypeHint(RequirementDetailsBean.class)
    Document<RequirementDetailsBean> getRequirement(@PathParam("requirementId") String requirementId);
}

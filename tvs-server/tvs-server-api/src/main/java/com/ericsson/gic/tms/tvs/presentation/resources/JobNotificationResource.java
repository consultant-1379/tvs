package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.presentation.dto.RecipientBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RuleBean;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/job-notifications/{jobId}")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface JobNotificationResource {

    String RULES = "/rules";
    String RULE = "/rules" + "/{ruleId}";
    String RECIPIENTS = "/recipients";
    String RECIPIENT = RECIPIENTS + "/{recipientId}";

    /**
     * <p>Retrieves the list of notification rules for given job</p>
     *
     * @param jobId required ID of job
     * @return the list of job notification rules
     */
    @GET
    @Path(RULES)
    @TypeHint(RuleBean.class)
    DocumentList<RuleBean> getRules(@PathParam("jobId") String jobId);

    /**
     * <p>Adds new notification rule to given job</p>
     *
     * @param jobId required ID of job
     * @param rule  required rule data
     * @return added rule bean
     */
    @POST
    @Path(RULES)
    @TypeHint(RuleBean.class)
    Document<RuleBean> addRule(@PathParam("jobId") String jobId,
                               @NotNull @Valid RuleBean rule);


    /**
     * <p>Updates notification rule by given rule ID and job ID</p>
     *
     * @param jobId  required ID of job
     * @param ruleId required ID of rule
     * @param rule   required rule data
     * @return added rule bean
     */
    @PUT
    @Path(RULE)
    @TypeHint(RuleBean.class)
    Document<RuleBean> updateRule(@PathParam("jobId") String jobId,
                                  @PathParam("ruleId") String ruleId,
                                  @NotNull @Valid RuleBean rule);

    /**
     * <p>Removes notification rule by given rule ID and job ID</p>
     *
     * @param jobId  required ID of job
     * @param ruleId required rule ID
     * @return removed rule bean
     */
    @DELETE
    @Path(RULE)
    @TypeHint(RuleBean.class)
    Document<RuleBean> removeRule(@PathParam("jobId") String jobId,
                                  @PathParam("ruleId") String ruleId);

    /**
     * <p>Retrieves the list of notification email recipients for given job</p>
     *
     * @param jobId required ID of job
     * @return the list of recipients
     */
    @GET
    @Path(RECIPIENTS)
    @TypeHint(RecipientBean.class)
    DocumentList<RecipientBean> getRecipients(@PathParam("jobId") String jobId);

    /**
     * <p>Adds email recipient to given job</p>
     *
     * @param jobId     required ID of job
     * @param recipient required recipient data
     * @return added recipient bean
     */
    @POST
    @Path(RECIPIENTS)
    @TypeHint(RecipientBean.class)
    Document<RecipientBean> addRecipient(@PathParam("jobId") String jobId,
                                         @NotNull @Valid RecipientBean recipient);

    /**
     * <p>Removes email recipient from given job</p>
     *
     * @param jobId       required ID of job
     * @param recipientId required recipient email address
     * @return removed recipient bean
     */
    @DELETE
    @Path(RECIPIENT)
    @TypeHint(RecipientBean.class)
    Document<RecipientBean> removeRecipient(@PathParam("jobId") String jobId,
                                            @PathParam("recipientId") String recipientId);


}

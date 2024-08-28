package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.CollectionMetadataService;
import com.ericsson.gic.tms.tvs.application.services.JobNotificationService;
import com.ericsson.gic.tms.tvs.domain.model.TvsCollections;
import com.ericsson.gic.tms.tvs.presentation.dto.RecipientBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RuleBean;
import com.ericsson.gic.tms.tvs.presentation.resources.JobNotificationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.COLUMNS;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         23/01/2017
 */

@Controller
public class JobNotificationController extends AbstractJsonApiCapableController implements JobNotificationResource {

    @Autowired
    private JobNotificationService jobNotificationService;

    @Autowired
    private CollectionMetadataService collectionMetadataService;

    @Override
    public DocumentList<RuleBean> getRules(String jobId) {

        List<RuleBean> rules = jobNotificationService.getRulesByJobId(jobId);

        return responseFor(rules)
            .withMeta(COLUMNS, collectionMetadataService.getColumns(TvsCollections.TEST_SESSION.getName()))
            .build();
    }

    @Override
    public Document<RuleBean> addRule(String jobId, RuleBean rule) {

        RuleBean saved = jobNotificationService.updateRule(jobId, null, rule);

        return responseFor(saved)
            .build();
    }

    @Override
    public Document<RuleBean> updateRule(String jobId, String ruleId, RuleBean rule) {

        RuleBean saved = jobNotificationService.updateRule(jobId, ruleId, rule);

        return responseFor(saved)
            .build();
    }

    @Override
    public Document<RuleBean> removeRule(String jobId, String ruleId) {

        RuleBean removed = jobNotificationService.removeRule(jobId, ruleId);

        return responseFor(removed)
            .build();

    }

    @Override
    public DocumentList<RecipientBean> getRecipients(String jobId) {

        return responseFor(jobNotificationService.getRecipientsByJobId(jobId)).build();
    }

    @Override
    public Document<RecipientBean> addRecipient(String jobId, RecipientBean recipient) {

        return responseFor(jobNotificationService.addRecipient(jobId, recipient)).build();
    }

    @Override
    public Document<RecipientBean> removeRecipient(String jobId, String recipientId) {

        return responseFor(jobNotificationService.removeRecipient(jobId, recipientId)).build();
    }
}

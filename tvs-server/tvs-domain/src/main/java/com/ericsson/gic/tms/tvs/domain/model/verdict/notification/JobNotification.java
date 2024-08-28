package com.ericsson.gic.tms.tvs.domain.model.verdict.notification;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         24/01/2017
 */
public class JobNotification implements MongoEntity<String> {

    @MongoId
    @MongoObjectId
    private String id;

    private String jobId;

    private List<Criteria> rules = newArrayList();

    private List<Recipient> recipients = newArrayList();

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public List<Criteria> getRules() {
        return rules;
    }

    public void setRules(List<Criteria> rules) {
        this.rules = rules;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}

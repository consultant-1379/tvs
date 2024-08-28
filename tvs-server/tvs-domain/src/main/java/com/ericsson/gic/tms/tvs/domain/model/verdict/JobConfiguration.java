package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

public class JobConfiguration implements MongoEntity<String> {

    @MongoId
    @MongoObjectId
    private String id;

    private String jobName;

    private String source;

    private String contextId;

    private int priority;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

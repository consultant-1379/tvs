package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

public class JobConfigurationBean implements Attributes<String> {

    private String jobName;

    private String source;

    private String contextId;

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

    @Override
    public String getId() {
        return jobName;
    }

    public void setId(String id) {
        // hack for jersey client
    }
}

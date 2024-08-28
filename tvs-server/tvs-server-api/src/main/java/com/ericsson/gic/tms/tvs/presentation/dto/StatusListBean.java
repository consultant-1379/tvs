package com.ericsson.gic.tms.tvs.presentation.dto;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@XmlRootElement
public class StatusListBean {

    private Set<String> statuses;

    public Set<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(Set<String> statuses) {
        this.statuses = statuses;
    }
}

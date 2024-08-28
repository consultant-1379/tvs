package com.ericsson.gic.tms.tvs.presentation.dto;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class ExecutionTimeBean implements ResetFields {

    @NotNull
    private Date startDate;

    private Date stopDate;

    private Long duration;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public void resetImmutableFields() {
        this.duration = null;
    }
}

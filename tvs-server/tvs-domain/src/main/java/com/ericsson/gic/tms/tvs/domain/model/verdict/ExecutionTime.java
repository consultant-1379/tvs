package com.ericsson.gic.tms.tvs.domain.model.verdict;

import java.util.Date;

public class ExecutionTime {

    private Date startDate;

    private Date stopDate;

    private long duration;

    public ExecutionTime() {
        // needed for parsing
    }

    public ExecutionTime(Date startDate, Date stopDate) {
        this.startDate = startDate;
        this.stopDate = stopDate;
    }

    public void prePersist() {
        if (stopDate != null && startDate != null) {
            this.duration = stopDate.getTime() - startDate.getTime();
        }
    }

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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}

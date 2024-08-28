package com.ericsson.gic.tms.tvs.presentation.dto;

import java.time.LocalDateTime;

public class AggregationInfoBean {

    private LocalDateTime lastRun;

    private boolean isRunning;

    public LocalDateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}

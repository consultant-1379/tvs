package com.ericsson.gic.tms.tvs.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncAggregationTriggerService {

    @Autowired
    private AggregationTriggerService aggregationTriggerService;

    @Async
    public void trigger() {
        aggregationTriggerService.trigger();
    }
}

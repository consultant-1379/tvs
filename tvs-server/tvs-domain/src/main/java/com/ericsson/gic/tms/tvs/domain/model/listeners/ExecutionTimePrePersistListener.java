package com.ericsson.gic.tms.tvs.domain.model.listeners;

import com.ericsson.gic.tms.tvs.domain.model.verdict.HasExecutionTime;
import org.springframework.stereotype.Component;

@Component
public class ExecutionTimePrePersistListener implements BeforeSaveListener<HasExecutionTime> {

    @Override
    public void onBeforeSave(HasExecutionTime source) {
        if (source.getTime() != null) {
            source.getTime().prePersist();
        }
    }
}

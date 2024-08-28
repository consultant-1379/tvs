package com.ericsson.gic.tms.tvs.domain.model.listeners;

import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JobUidGenerator implements BeforeSaveListener<Job> {

    @Override
    public void onBeforeSave(Job source) {
        if (source.getUid() == null) {
            String uid = UUID.randomUUID().toString();
            source.setUid(uid);
        }
    }

}

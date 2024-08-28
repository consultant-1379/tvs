package com.ericsson.gic.tms.tvs.domain.model.listeners;

import com.ericsson.gic.tms.tvs.domain.model.verdict.AuditAware;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuditDateGenerator implements BeforeSaveListener<AuditAware> {

    @Override
    public void onBeforeSave(AuditAware source) {
        Date now = new Date();
        if (source.getCreatedDate() == null) {
            source.setCreatedDate(now);
        }

        source.setModifiedDate(now);
    }

}

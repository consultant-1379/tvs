package com.ericsson.gic.tms.tvs.domain.model.verdict;

import java.util.Date;

public interface AuditAware {

    Date getCreatedDate();
    void setCreatedDate(Date time);

    Date getModifiedDate();
    void setModifiedDate(Date time);
}

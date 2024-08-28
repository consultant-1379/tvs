package com.ericsson.gic.tms.tvs.infrastructure;

import com.ericsson.gic.tms.infrastructure.AbstractMongoQueryService;
import com.ericsson.gic.tms.infrastructure.ApplicationContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoQueryService extends AbstractMongoQueryService<TvsQueryFile> {

    @Autowired
    public MongoQueryService(ApplicationContextHelper contextHelper) {
        super(contextHelper, TvsQueryFile.class);
    }
}

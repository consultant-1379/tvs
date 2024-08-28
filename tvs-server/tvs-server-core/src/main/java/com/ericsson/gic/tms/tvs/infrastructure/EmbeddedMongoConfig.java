package com.ericsson.gic.tms.tvs.infrastructure;

import com.ericsson.gic.tms.infrastructure.AbstractMongoConfig;
import com.ericsson.gic.tms.infrastructure.EmbeddedMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

import static com.ericsson.gic.tms.infrastructure.Profiles.*;
import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;

@Configuration
@Profile(DEVELOPMENT)
public class EmbeddedMongoConfig extends AbstractMongoConfig {

    @Autowired
    private EmbeddedMongo embeddedMongo;

    @PostConstruct
    protected void runEmbeddedMongo() throws IOException {
        embeddedMongo.run(
            JOB.getName(),
            TEST_SESSION.getName(),
            TEST_SUITE_RESULT.getName(),
            TEST_CASE_RESULT.getName()
        );
    }

    @PreDestroy
    protected void stopEmbeddedMongo() {
        embeddedMongo.stop();
    }
}

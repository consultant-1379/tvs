package com.ericsson.gic.tms.tvs.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.ericsson.gic.tms.infrastructure.Profiles.*;

@Component
@Profile(DEVELOPMENT)
public class MongoFixturesInitializer {

    private final Logger logger = LoggerFactory.getLogger(MongoFixturesInitializer.class);

    @Value("${mongodb.import.path:#{null}}")
    private String importPath;

    @Autowired
    private MongoFixtures mongoFixtures;

    @PostConstruct
    public void runFixtures() throws Exception {
        if (importPath != null) {
            logger.warn("Path for mongoimport is set; skipping fixtures from classpath");
            return;
        }
        mongoFixtures.resetCollections();
    }
}

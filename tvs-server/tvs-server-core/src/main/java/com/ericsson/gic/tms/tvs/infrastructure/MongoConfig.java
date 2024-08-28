package com.ericsson.gic.tms.tvs.infrastructure;

import com.ericsson.gic.tms.infrastructure.AbstractMongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.ericsson.gic.tms.infrastructure.Profiles.*;

@Configuration
@Profile("!" + DEVELOPMENT)
public class MongoConfig extends AbstractMongoConfig {
}

package com.ericsson.gic.tms.tvs.infrastructure;

import com.ericsson.gic.tms.tvs.infrastructure.changelog.ChangeLogMarker;
import com.github.mongobee.Mongobee;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MongobeeConfiguration {

    @Value("${spring.data.mongodb.database}")
    protected String dbName;

    @Bean
    @Autowired
    public Mongobee mongobee(Mongo mongo, Environment environment) {
        Mongobee runner = new Mongobee((MongoClient) mongo);
        runner.setDbName(dbName);
        runner.setChangeLogsScanPackage(ChangeLogMarker.class.getPackage().getName());
        runner.setSpringEnvironment(environment);

        return runner;
    }
}

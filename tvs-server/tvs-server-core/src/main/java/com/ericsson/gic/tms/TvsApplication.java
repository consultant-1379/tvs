package com.ericsson.gic.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = EmbeddedMongoAutoConfiguration.class)
@EnableAsync
@EnableScheduling
public class TvsApplication extends SpringBootServletInitializer {

    public static void main(String... args) throws Exception {
        new SpringApplication(TvsApplication.class).run(args);
    }

}

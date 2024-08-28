package com.ericsson.gic.tms.tvs.infrastructure;

import com.ericsson.gic.tms.tvs.infrastructure.parser.DateDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JacksonConfig {

    @Bean
    public Module provideDateModule() {
        return new SimpleModule()
            .addDeserializer(Date.class, new DateDeserializer());
    }

}

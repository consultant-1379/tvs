package com.ericsson.gic.tms.tvs.infrastructure;

import com.ericsson.gic.tms.infrastructure.AbstractJerseyConfig;
import com.ericsson.gic.tms.tvs.infrastructure.provider.CsvMessageBodyWriter;
import com.ericsson.gic.tms.tvs.presentation.controllers.ControllersMarker;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.google.common.collect.Sets.newConcurrentHashSet;

@Configuration
public class JerseyConfig extends AbstractJerseyConfig {

    @Override
    protected void initialize() throws IOException {
        registerControllers(ControllersMarker.class);
        register(CsvMessageBodyWriter.class);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> container.setErrorPages(newConcurrentHashSet());
    }
}


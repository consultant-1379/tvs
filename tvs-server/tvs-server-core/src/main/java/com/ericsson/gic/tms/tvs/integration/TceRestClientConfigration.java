package com.ericsson.gic.tms.tvs.integration;

import com.ericsson.gic.tms.RestClientFacade;
import com.ericsson.gic.tms.presentation.resources.ContextResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ericsson.gic.tms.tvs.integration.RestClientFacadeProvider.*;

@Configuration
public class TceRestClientConfigration {

    @Autowired
    @Qualifier(TCE)
    private RestClientFacade restClientFacade;

    @Bean
    public ContextResource provideContextResource() {
        return restClientFacade.getResource(ContextResource.class);
    }

}

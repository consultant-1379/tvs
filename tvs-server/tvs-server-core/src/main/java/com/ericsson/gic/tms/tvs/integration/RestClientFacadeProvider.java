package com.ericsson.gic.tms.tvs.integration;

import com.ericsson.gic.tms.RestClientFacade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientFacadeProvider {

    public static final String TCE = "TCE";

    @Bean
    @Qualifier(TCE)
    public RestClientFacade providTvseRestClientFacade(@Value("${gtms.tce.url}") String endPoint) {
        return new RestClientFacade(endPoint);
    }

}

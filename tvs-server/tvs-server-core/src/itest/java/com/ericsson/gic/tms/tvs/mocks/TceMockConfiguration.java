package com.ericsson.gic.tms.tvs.mocks;

import com.ericsson.gic.tms.presentation.resources.ContextResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.*;

@Configuration
public class TceMockConfiguration {

    @Bean
    @Primary
    public ContextResource provideTestCaseService() {
        return mock(ContextResource.class);
    }

}

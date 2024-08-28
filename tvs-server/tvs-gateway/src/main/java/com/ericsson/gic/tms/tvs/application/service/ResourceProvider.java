package com.ericsson.gic.tms.tvs.application.service;

import com.ericsson.gic.tms.RestClientFacade;
import com.ericsson.gic.tms.tvs.presentation.resources.JobInsertResource;
import com.ericsson.gic.tms.tvs.presentation.resources.JobResource;
import com.ericsson.gic.tms.tvs.presentation.resources.TestCaseResultResource;
import com.ericsson.gic.tms.tvs.presentation.resources.TestSessionResource;
import com.ericsson.gic.tms.tvs.presentation.resources.TestSuiteResultResource;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class ResourceProvider {

    private static final Logger LOGGER = Logger.getLogger(ResourceProvider.class.getName());

    public WebTarget provideRestClient(String url) throws NoSuchAlgorithmException {
        return ClientBuilder.newBuilder()
            .sslContext(SSLContext.getInstance("SSL"))
            .register(new LoggingFilter(LOGGER, true))
            .build()
            .target(url);
    }

    public RestClientFacade getRestClientFacade(WebTarget target) {
        return new RestClientFacade(target);
    }

    public JobInsertResource getJobInsertResource(RestClientFacade restClientFacade) {
        return restClientFacade.getResource(JobInsertResource.class);
    }

    public TestCaseResultResource getTestCaseResultResource(RestClientFacade restClientFacade) {
        return restClientFacade.getResource(TestCaseResultResource.class);
    }

    public JobResource getJobResource(RestClientFacade restClientFacade) {
        return restClientFacade.getResource(JobResource.class);
    }

    public TestSuiteResultResource getTestSuiteResultResource(RestClientFacade restClientFacade) {
        return restClientFacade.getResource(TestSuiteResultResource.class);
    }

    public TestSessionResource getTestSessionResource(RestClientFacade restClientFacade) {
        return restClientFacade.getResource(TestSessionResource.class);
    }
}

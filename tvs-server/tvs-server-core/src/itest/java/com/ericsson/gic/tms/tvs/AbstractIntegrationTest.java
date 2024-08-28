package com.ericsson.gic.tms.tvs;

import com.ericsson.gic.tms.TvsApplication;
import com.ericsson.gic.tms.presentation.util.JsonApiUtil;
import com.ericsson.gic.tms.tvs.infrastructure.MongoFixtures;
import com.github.mongobee.Mongobee;
import com.google.common.collect.ImmutableMap;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TvsApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    protected JsonApiUtil mockedJsonApiUtil;

    @Autowired
    protected MongoFixtures mongoFixtures;

    @Autowired
    protected Mongobee mongobee;

    @Before
    public void setUpHateoas() throws Exception {
        mockedJsonApiUtil = spy(JsonApiUtil.class);
        doReturn(new Link("mocked link")).when(mockedJsonApiUtil).getLink(any(), any(), any(), any());
        doReturn(ImmutableMap.builder().build()).when(mockedJsonApiUtil).getPathParameters(any());
        doReturn(new MultivaluedStringMap()).when(mockedJsonApiUtil).getQueryParameters(any());
    }

    @Before
    public void setUpFixtures() throws Exception {
        mongoFixtures.resetCollections();
        mongobee.execute();
    }

}

package com.ericsson.gic.tms.tvs.presentation.controllers;


import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.UrlCountEntity;
import com.ericsson.gic.tms.tvs.domain.model.verdict.UserSessionEntity;
import com.ericsson.gic.tms.tvs.domain.repositories.UrlCountRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.UserSessionRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsObject;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class StatisticsControllerITest extends AbstractIntegrationTest {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UrlCountRepository urlCountRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Before
    public void setUp() throws Exception {
        userSessionRepository.save(createUserSession("ted"));
        userSessionRepository.save(createUserSession("ted"));
        userSessionRepository.save(createUserSession("ted"));
        userSessionRepository.save(createUserSession("ted"));

        urlCountRepository.save(createUrlData("jobs", 7));
        urlCountRepository.save(createUrlData("test", 4));
        urlCountRepository.save(createUrlData("report", 109));
        urlCountRepository.save(createUrlData("run", 10000));
    }

    @Test
    public void shouldGetUserStatistics() throws Exception {
        StatisticsObject[] forObject = testRestTemplate.getForObject("/metrics/users", StatisticsObject[].class);
        List<StatisticsObject> statisticsObjects = Arrays.asList(forObject);

        assertThat(statisticsObjects.size()).isEqualTo(1);

        StatisticsObject statisticsObject = statisticsObjects.get(statisticsObjects.size() - 1);

        assertThat(statisticsObject.getValue()).isEqualTo(4);
    }

    @Test
    public void shouldGetUrlStatistics() throws Exception {
        StatisticsObject[] forObject = testRestTemplate.getForObject("/metrics/url", StatisticsObject[].class);
        List<StatisticsObject> statisticsObjects = Arrays.asList(forObject);

        assertThat(statisticsObjects.size()).isEqualTo(5);

        assertThat(statisticsObjects.get(0).getValue()).isEqualTo(7);
        assertThat(statisticsObjects.get(1).getValue()).isEqualTo(4);
        assertThat(statisticsObjects.get(2).getValue()).isEqualTo(109);
        assertThat(statisticsObjects.get(3).getValue()).isEqualTo(10000);
    }

    private UserSessionEntity createUserSession(String username) {
        UserSessionEntity userSessionEntity = new UserSessionEntity();
        userSessionEntity.setCreatedAt(new Date());
        userSessionEntity.setSessionId("test");
        userSessionEntity.setUsername(username);

        return userSessionEntity;
    }

    private UrlCountEntity createUrlData(String url, int count) {
        DateTime dateTime = DateTime.now();
        UrlCountEntity urlCountEntity = new UrlCountEntity();
        urlCountEntity.setCount(count);
        urlCountEntity.setUrl(url);
        urlCountEntity.setMonth(dateTime.getMonthOfYear());
        urlCountEntity.setYear(dateTime.getYear());
        urlCountEntity.setDate(dateTime.withDayOfMonth(1).toDate());

        return urlCountEntity;
    }
}

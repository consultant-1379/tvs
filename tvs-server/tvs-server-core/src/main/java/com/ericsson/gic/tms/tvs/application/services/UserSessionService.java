package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.UserMetricsObject;
import com.ericsson.gic.tms.tvs.domain.model.verdict.UserSessionEntity;
import com.ericsson.gic.tms.tvs.domain.repositories.UserSessionRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.Month;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSessionService {

    static final int PREVIOUS_MONTHS = 8;

    @Autowired
    private UserSessionRepository userSessionRepository;

    public void addUserSession(String sessionId, String userName) {
        UserSessionEntity userSessionEntity = new UserSessionEntity();
        userSessionEntity.setUsername(userName);
        userSessionEntity.setSessionId(sessionId);
        userSessionEntity.setCreatedAt(new Date());

        userSessionRepository.save(userSessionEntity);
    }

    public List<StatisticsObject> getLoggedOnUsers() {
        DateTime dateTime = new DateTime(new Date());
        List<UserMetricsObject> byMonth = userSessionRepository
                .findByDate(dateTime.minusMonths(PREVIOUS_MONTHS).toDate());
        return byMonth.stream()
                .map(item -> new StatisticsObject(Month.getMonthName(item.getMonth()) + " " +
                        item.getYear(), item.getValue()))
                .collect(Collectors.toList());
    }
}

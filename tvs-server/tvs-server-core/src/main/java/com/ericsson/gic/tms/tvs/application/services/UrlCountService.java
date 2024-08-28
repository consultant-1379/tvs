package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.UrlCountEntity;
import com.ericsson.gic.tms.tvs.domain.repositories.UrlCountRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UrlCountService {

    static final int PREVIOUS_MONTHS = 6;

    @Autowired
    private UrlCountRepository urlCountRepository;

    public void increment(String url) {
        DateTime dateTime = new DateTime(new Date());
        Optional<UrlCountEntity> urlCount = Optional.ofNullable(
            urlCountRepository.getByUrlMonthYear(url, dateTime.getMonthOfYear(), dateTime.getYear()));

        if (urlCount.isPresent()) {
            UrlCountEntity countEntity = urlCount.get();
            int count = countEntity.getCount();
            countEntity.setCount(++count);
            urlCountRepository.save(countEntity);
        } else {
            UrlCountEntity urlCountEntity = new UrlCountEntity();
            urlCountEntity.setCount(1);
            urlCountEntity.setMonth(dateTime.getMonthOfYear());
            urlCountEntity.setYear(dateTime.getYear());
            urlCountEntity.setUrl(url);
            urlCountEntity.setDate(dateTime.withDayOfMonth(1).toDate());
            urlCountRepository.save(urlCountEntity);
        }
    }

    public List<StatisticsObject> getUrlHitCount() {
        DateTime dateTime = new DateTime(new Date());
        List<UrlCountEntity> byPreviousMonthYear = urlCountRepository
            .findByPreviousMonthYear(dateTime.minusMonths(PREVIOUS_MONTHS).withDayOfMonth(1).toDate());
        return byPreviousMonthYear.stream()
                .map(item -> new StatisticsObject(item.getUrl(), item.getCount()))
                .collect(Collectors.toList());
    }
}

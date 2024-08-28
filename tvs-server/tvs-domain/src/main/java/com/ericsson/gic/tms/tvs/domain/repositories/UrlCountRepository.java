package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.verdict.UrlCountEntity;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.URL_USAGE;
import static com.google.common.collect.Lists.newArrayList;

@Repository
public class UrlCountRepository extends BaseJongoRepository<UrlCountEntity, String> {

    @Override
    protected String getCollectionName() {
        return URL_USAGE.getName();
    }

    public UrlCountRepository() {
        super(UrlCountEntity.class);
    }

    public UrlCountEntity getByUrlMonthYear(String url, int month, int year) {
        return findOneBy("{url: #, month: #, year: #}", url, month, year);
    }

    public List<UrlCountEntity> findByPreviousMonthYear(Date date) {
        return newArrayList(findBy("{ date: {$gte:#}}}", date));
    }
}

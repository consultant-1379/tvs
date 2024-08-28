package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import com.google.common.base.Objects;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Date;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;

/**
 * Created by egergle on 23/10/2017.
 */
public class UrlCountEntity implements MongoEntity<String> {

    @MongoId
    @MongoObjectId
    private String id;

    private int month;

    private int year;

    private int count;

    private String url;

    private Date date;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id, url, month);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        UrlCountEntity userEntity = (UrlCountEntity) other;
        return equal(id, userEntity.id) &&
                equal(url, userEntity.url) &&
                equal(month, userEntity.month) &&
                equal(year, userEntity.year) &&
                equal(count, userEntity.count);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("url", url)
                .add("month", month)
                .add("year", year)
                .add("count", count)
                .toString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

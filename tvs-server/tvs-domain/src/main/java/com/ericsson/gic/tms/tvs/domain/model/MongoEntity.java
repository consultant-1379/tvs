package com.ericsson.gic.tms.tvs.domain.model;

public interface MongoEntity<T> {
    T getId();
    void setId(T id);
}

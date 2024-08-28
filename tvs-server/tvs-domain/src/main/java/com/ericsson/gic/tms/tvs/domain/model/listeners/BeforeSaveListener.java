package com.ericsson.gic.tms.tvs.domain.model.listeners;

public interface BeforeSaveListener<T> {

    void onBeforeSave(T source);
}

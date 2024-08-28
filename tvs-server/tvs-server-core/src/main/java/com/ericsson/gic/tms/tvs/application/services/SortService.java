package com.ericsson.gic.tms.tvs.application.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SortService {

    private static final String ISO_VERSION = "ISO_VERSION";
    private static final String ISO_VERSION_PADDED = "ISO_VERSION_PADDED";

    public Sort getSort(String orderMode, String orderBy) {
        String order = orderBy;

        if (order.equals(ISO_VERSION)) {
            order = ISO_VERSION_PADDED;
        }

        if (!StringUtils.isEmpty(orderBy) && "id".equals(order)) {
            order = "_id"; //workaround for _id sorting
        }

        return new Sort(new Sort.Order(
            Sort.Direction.fromStringOrNull(orderMode), order));
    }
}

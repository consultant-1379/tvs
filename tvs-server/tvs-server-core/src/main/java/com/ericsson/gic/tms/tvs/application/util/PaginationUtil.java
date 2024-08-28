package com.ericsson.gic.tms.tvs.application.util;

import java.util.List;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Lists.*;
import static org.springframework.util.CollectionUtils.*;

public final class PaginationUtil {

    private PaginationUtil() {
        // No instance is allowed
    }

    public static <T> List<T> getPaginatedItems(final List<T> data, final int page, final int pageSize) {

        checkArgument(pageSize > 0, "Page-size cannot be less than 1");
        checkArgument(page >= 0, "Page cannot be less than 0");

        List<T> out = newArrayList();
        if (isEmpty(data)) {
            return out;
        }

        final int total = data.size();
        final int totalNumberOfPages = (int) Math.ceil(total / (double) pageSize);

        // return data if page out of total pages bound
        if (totalNumberOfPages < page) {
            return out;
        }

        // set first returned result position
        final int fromIndex = Math.max(0, page * pageSize);

        // set max return result position
        final int toIndex = Math.min(total, (page + 1) * pageSize);

        return data.subList(fromIndex, toIndex);
    }
}

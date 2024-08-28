package com.ericsson.gic.tms.tvs.domain.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static java.util.stream.Collectors.*;

public class Query {
    private Map<String, List<Filter>> filters;

    public Query() {
        this.filters = newHashMap();
    }

    public Query(Query query) {
        this(query.getCopy());
    }

    public Query(Filter... filters) {
        this(newArrayList(filters));
    }

    public Query(Collection<Filter> filters) {
        this.filters = filters.stream()
            .collect(groupingBy(Filter::getField));
    }

    private List<Filter> getCopy() {
        return filters.values().stream().flatMap(List::stream).collect(toList());
    }

    public List<MongoExpression> getFilters() {
        return filters.values().stream()
            .map(list -> {
                if (list.size() > 1) {
                    return new AndFilter(list);
                } else {
                    return list.get(0);
                }
            })
            .collect(toList());
    }

    public void setFilters(Map<String, List<Filter>> filters) {
        this.filters = filters;
    }

    public Query addFilter(Filter filter) {
        List<Filter> filterList = this.filters.get(filter.getField());
        if (filterList == null) {
            filterList = newArrayList();
        }
        filterList.add(filter);
        this.filters.put(filter.getField(), filterList);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for (MongoExpression filter : getFilters()) {
            sb.append(filter.getExpression())
                .append(',');
        }

        if (!getFilters().isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.append("}").toString();
    }

    public List<Object> getQueryParameters() {
        return getFilters().stream()
            .map(MongoExpression::getValues)
            .flatMap(arg -> {
                if (arg instanceof Object[]) {
                    return Stream.of((Object[]) arg);
                }
                return Stream.of(arg);
            })
            .collect(toList());
    }
}

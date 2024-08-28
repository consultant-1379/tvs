package com.ericsson.gic.tms.tvs.domain.util;

import java.util.List;

import static java.util.stream.Collectors.*;

public class AndFilter implements MongoExpression {

    private List<Filter> filters;

    public AndFilter(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public String getExpression() {
        if (filters.isEmpty()) {
            throw new IllegalStateException();
        } else if (filters.size() == 1) {
            return filters.get(0).getExpression();
        }

        List<String> collect = filters.stream()
            .map(filter -> "{" + filter.getExpression() + "}")
            .collect(toList());

        String joined = String.join(",", collect);

        return "$and: [" + joined + "]";
    }

    @Override
    public Object getValues() {
        if (filters.isEmpty()) {
            throw new IllegalStateException();
        } else if (filters.size() == 1) {
            return filters.get(0).getValues();
        }

        return filters.stream()
            .map(Filter::getValues)
            .collect(toList())
            .toArray();
    }

    public List<Filter> getFilters() {
        return filters;
    }

}

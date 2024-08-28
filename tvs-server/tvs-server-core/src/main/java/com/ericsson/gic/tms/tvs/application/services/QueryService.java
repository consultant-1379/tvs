package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.domain.model.verdict.notification.Criteria;
import com.ericsson.gic.tms.tvs.domain.util.Filter;
import com.ericsson.gic.tms.tvs.domain.util.Operation;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.domain.util.RangeFilter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class QueryService {

    private Pattern pattern = Pattern.compile("([a-zA-Z0-9\\._]+)([~=<>])([a-z]+)\\|(.+)");

    private enum Token {
        KEY(1), OPERATION(2), TYPE(3), VALUE(4);
        private int index;

        Token(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    private Map<String, Function<String[], Collection<Filter>>> transformerMap = ImmutableMap
        .<String, Function<String[], Collection<Filter>>>builder()
        .put("string", this::handleString)
        .put("number", this::handleNumber)
        .put("date", this::handleDate)
        .build();

    private Map<String, Operation> operationMap = Stream.of(Operation.values())
        .filter(operation -> operation.getOperationSymbol() != null)
        .collect(toMap(Operation::getOperationSymbol, operation -> operation));

    private Collection<Filter> handleString(String[] params) {
        String field = params[0];
        String value = params[2];
        String operationKey = params[1];

        if (value.startsWith("[") && value.endsWith("]") &&
            Operation.CONTAINS.getOperationSymbol().equals(operationKey)) {

            String[] substring = value.substring(1, value.length() - 1).split(",");
            return singleton(Operation.IN.createFilter(field, Lists.newArrayList(substring)));
        }

        return singleton(operationMap.get(operationKey).createFilter(field, value));
    }

    private Collection<Filter> handleNumber(String[] params) {
        return singleton(operationMap.get(params[1]).createFilter(params[0], parseNumber(params[2])));
    }

    private Collection<Filter> handleDate(String[] params) {
        if (Operation.EQUALS.getOperationSymbol().equals(params[1])) {
            LocalDateTime parse = LocalDateTime.parse(params[2]).truncatedTo(ChronoUnit.DAYS);
            return singleton(new RangeFilter(params[0], DateUtils.toDate(parse), DateUtils.toDate(parse.plusDays(1))));
        }
        return singleton(operationMap.get(params[1]).createFilter(params[0], parseDate(params[2])));
    }

    private Collection<Filter> handleMatchResult(MatchResult result) {
        return ruleToFilters(new Criteria(
            result.group(Token.KEY.getIndex()),
            result.group(Token.TYPE.getIndex()),
            result.group(Token.OPERATION.getIndex()),
            result.group(Token.VALUE.getIndex())
        ));
    }

    public Collection<Filter> ruleToFilters(Criteria rule) {
        if (transformerMap.get(rule.getFieldType()) == null) {
            return Collections.emptyList();
        }

        String key = rule.getField();
        if (!StringUtils.isEmpty(key) && "id".equals(key)) {
            key = "_id"; //workaround for _id filtration
        }

        return transformerMap.get(rule.getFieldType())
            .apply(new String[]{
                key,
                rule.getOperation(),
                rule.getValue().toString()});
    }

    public Query createQuery(String query) {
        if (!Strings.isNullOrEmpty(query)) {
            List<Filter> filters = Stream.of(query.split("&"))
                .map(pattern::matcher)
                .filter(Matcher::matches)
                .map(Matcher::toMatchResult)
                .map(this::handleMatchResult)
                .flatMap(Collection::stream)
                .collect(toList());
            return new Query(filters);
        }

        return new Query();
    }

    public Query createQuery(List<Criteria> rules) {
        List<Filter> filters = createFilters(rules);
        return new Query(filters);
    }

    private List<Filter> createFilters(List<Criteria> rules) {
        return rules.stream()
            .map(this::ruleToFilters)
            .flatMap(Collection::stream)
            .collect(toList());
    }

    private Object parseDate(String value) {
        try {
            return DateUtils.toDate(LocalDateTime.parse(value));
        } catch (DateTimeParseException e) {
            return value;
        }
    }

    private Object parseNumber(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}

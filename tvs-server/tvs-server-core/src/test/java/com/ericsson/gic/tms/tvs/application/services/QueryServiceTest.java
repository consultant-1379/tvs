package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.domain.util.AndFilter;
import com.ericsson.gic.tms.tvs.domain.util.ContainsFilter;
import com.ericsson.gic.tms.tvs.domain.util.EqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.Filter;
import com.ericsson.gic.tms.tvs.domain.util.GreaterThanFilter;
import com.ericsson.gic.tms.tvs.domain.util.InFilter;
import com.ericsson.gic.tms.tvs.domain.util.LessThanFilter;
import com.ericsson.gic.tms.tvs.domain.util.MongoExpression;
import com.ericsson.gic.tms.tvs.domain.util.Operation;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.domain.util.RangeFilter;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryServiceTest {

    private QueryService queryService = new QueryService();

    @Test
    public void testValidStringQuery() {
        String query = "name~string|text";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(1);

        Filter filter = (Filter) parsed.getFilters().get(0);

        assertThat(filter)
            .isNotNull()
            .isInstanceOf(ContainsFilter.class);

        assertThat(filter.getField())
            .isEqualTo("name");

        assertThat(filter.getOperation())
            .isEqualTo(Operation.CONTAINS);

        assertThat(filter.getValues())
            .isNotNull()
            .isEqualTo("\\Qtext\\E");

        assertThat(parsed.toString())
            .isEqualTo("{name: {$regex: #, $options: 'i'}}");

        assertThat(parsed.getQueryParameters())
            .hasSize(1)
            .containsExactly("\\Qtext\\E");
    }

    @Test
    public void testValidNumberQuery() {
        String query = "total=number|56";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(1);

        Filter filter = (Filter) parsed.getFilters().get(0);

        assertThat(filter)
            .isNotNull()
            .isInstanceOf(EqualsFilter.class);

        assertThat(filter.getField())
            .isEqualTo("total");

        assertThat(filter.getOperation())
            .isEqualTo(Operation.EQUALS);

        assertThat(filter.getValues())
            .isNotNull()
            .isEqualTo(56L);

        assertThat(parsed.toString())
            .isEqualTo("{total: #}");

        assertThat(parsed.getQueryParameters())
            .hasSize(1)
            .containsExactly(56L);
    }

    @Test
    public void testValidDateQuery() {
        String query = "startDate>date|2015-12-12T12:12:12";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(1);

        Filter filter = (Filter) parsed.getFilters().get(0);

        assertThat(filter)
            .isNotNull()
            .isInstanceOf(GreaterThanFilter.class);

        assertThat(filter.getField())
            .isEqualTo("startDate");

        assertThat(filter.getOperation())
            .isEqualTo(Operation.GREATER_THAN);

        Date expected = DateUtils.toDate(LocalDateTime.parse("2015-12-12T12:12:12"));
        assertThat(filter.getValues())
            .isNotNull()
            .isEqualTo(expected);

        assertThat(parsed.toString())
            .isEqualTo("{startDate: {$gt: #}}");

        assertThat(parsed.getQueryParameters())
            .hasSize(1)
            .containsExactly(expected);
    }

    @Test
    public void testValidEqualsDateQuery() {
        String query = "startDate=date|2015-12-12T12:12:12";

        LocalDateTime from = LocalDateTime.of(2015, 12, 12, 0, 0);
        LocalDateTime to = from.plusDays(1);

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(1)
            .extracting(Object::getClass)
            .containsExactly(RangeFilter.class);

        MongoExpression filter = parsed.getFilters().get(0);

        assertThat((Object[]) filter.getValues())
            .hasSize(2)
            .containsExactly(DateUtils.toDate(from), DateUtils.toDate(to));
    }

    @Test
    public void testValidArrayQuery() {
        String query = "time.start~string|[a,b,c]";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(1)
            .extracting(Object::getClass)
            .containsExactly(InFilter.class);

        MongoExpression filter = parsed.getFilters().get(0);

        assertThat(filter.getValues())
            .isNotNull()
            .isInstanceOf(ArrayList.class);

        assertThat((List<String>) filter.getValues())
            .hasSize(3)
            .containsExactly("a", "b", "c");
    }

    @Test
    public void testValidCompoundQuery() {
        String query = "startDate>date|2015-12-12T12:12:12&name~string|sample\\+secondPart&testCaseCount<number|5";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(3)
            .extracting(MongoExpression::getClass)
            .contains(GreaterThanFilter.class, ContainsFilter.class, LessThanFilter.class);

        assertThat(parsed.toString())
            .contains("startDate: {$gt: #}", "name: {$regex: #, $options: 'i'}", "testCaseCount: {$lt: #}");

        assertThat(parsed.getQueryParameters())
            .contains(DateUtils.toDate(LocalDateTime.parse("2015-12-12T12:12:12")),
                "\\Qsample\\+secondPart\\E", 5L);
    }

    @Test
    public void testContainsSpecialCharactersQuery() {
        String query = "name~string|testValue\\$KGB\\+N";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
                .isNotNull();

        assertThat(parsed.getQueryParameters().get(0).toString())
                .contains("testValue\\$KGB\\+N");
    }

    @Test
    public void testInvalidQuery() {
        String query = "<script>alert('Hello');</script>";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(0);
    }

    @Test
    public void testPartiallyInvalidQuery() {
        String query = "startDate>=numbario|strangeTextiaej...//";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(0);
    }

    @Test
    public void testAndExpression() {
        String query = "id~string|blabla&id~string|agaga&testCaseCount<number|5";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed)
            .isNotNull();

        assertThat(parsed.getFilters())
            .hasSize(2)
            .extracting(MongoExpression::getClass)
            .containsOnly(AndFilter.class, LessThanFilter.class);

        assertThat(parsed.toString())
            .contains("$and: [{_id: {$regex: #, $options: 'i'}},{_id: {$regex: #, $options: 'i'}}]",
                "testCaseCount: {$lt: #}");

        assertThat(parsed.getQueryParameters())
            .containsOnly("\\Qblabla\\E", "\\Qagaga\\E", 5L);
    }

    @Test
    public void testFieldWithUnderscore() throws Exception {
        String query = "ISO_VERSION~string|42";

        Query parsed = queryService.createQuery(query);

        assertThat(parsed.getFilters())
            .hasSize(1);

        assertThat(((Filter) parsed.getFilters().get(0)).getField())
            .isEqualTo("ISO_VERSION");
    }
}

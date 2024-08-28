package com.ericsson.gic.tms.tvs.application.util;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static com.ericsson.gic.tms.tvs.application.util.PaginationUtil.*;
import static com.google.common.collect.Lists.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

public class PaginationUtilTest {

    private static final int FIRST_PAGE = 0; // internally start with 0
    private static final int SECOND_PAGE = 1; // internally start with 0
    private static final int DEFAULT_SIZE = 5; // internally start with 0

    private List<String> subData1;
    private List<String> subData2;
    private List<String> allData;

    @Before
    public void before() {
        subData1 = newArrayList("1", "2.0", "1.11", "2.11", "1.2");
        subData2 = newArrayList("2", "2.1", "2.12", "1.1");
        allData = Stream.concat(subData1.stream(), subData2.stream()).collect(toList());
    }

    @Test
    public void testFirstPageOfDataItems() {
        List<String> paginatedData = getPaginatedItems(allData, FIRST_PAGE, DEFAULT_SIZE);
        assertThat(paginatedData)
            .hasSize(DEFAULT_SIZE)
            .containsOnlyElementsOf(subData1);
    }

    @Test
    public void testLastPageOfDataItems() {
        List<String> paginatedData = getPaginatedItems(allData, SECOND_PAGE, DEFAULT_SIZE);
        assertThat(paginatedData)
            .containsOnlyElementsOf(subData2);
    }

    @Test
    public void testNonexistentPageOfDataItems() {
        List<String> paginatedData = getPaginatedItems(allData, 999, DEFAULT_SIZE);
        assertThat(paginatedData).isEmpty();
    }

    @Test
    public void testEmptyPagedDataItems() {
        List<String> paginatedData = getPaginatedItems(null, FIRST_PAGE, DEFAULT_SIZE);
        assertThat(paginatedData).isEmpty();
    }
}

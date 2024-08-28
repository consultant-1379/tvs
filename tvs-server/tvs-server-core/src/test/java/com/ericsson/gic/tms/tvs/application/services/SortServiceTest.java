package com.ericsson.gic.tms.tvs.application.services;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.*;

public class SortServiceTest {

    private SortService sortService = new SortService();

    @Test
    public void testIdFieldSort() {
        String field = "id";

        Sort asc = sortService.getSort("ASC", field);
        assertThat(asc)
            .isNotNull();

        assertThat(asc.getOrderFor("_id"))
            .isNotNull();
    }

    @Test
    public void testUnderscoreIdFieldSort() {
        String field = "_id";

        Sort asc = sortService.getSort("ASC", field);
        assertThat(asc)
            .isNotNull();

        assertThat(asc.getOrderFor(field))
            .isNotNull();
    }

    @Test
    public void testAnotherFieldSort() {
        String field = "another";

        Sort asc = sortService.getSort("ASC", field);
        assertThat(asc)
            .isNotNull();

        assertThat(asc.getOrderFor(field))
            .isNotNull();
    }

}

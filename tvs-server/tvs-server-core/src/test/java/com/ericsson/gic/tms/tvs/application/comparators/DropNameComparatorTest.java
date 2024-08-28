package com.ericsson.gic.tms.tvs.application.comparators;

import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop.JobDropReport;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

public class DropNameComparatorTest {

    private DropNameComparator comparator;

    @Before
    public void before() {
        comparator = new DropNameComparator();
    }

    @Test
    public void testSimpleDropNameComparision() {
        assertThat(comparator.compare(new JobDropReport("1.0"), new JobDropReport("1.1")) < 0)
            .isTrue();
        assertThat(comparator.compare(new JobDropReport("1.1.1"), new JobDropReport("1.1")) > 0)
            .isTrue();
        assertThat(comparator.compare(new JobDropReport("1.0"), new JobDropReport("1.0")) == 0)
            .isTrue();
        assertThat(comparator.compare(new JobDropReport("1.0.0"), new JobDropReport("1.0")) == 0)
            .isTrue();
        assertThat(comparator.compare(new JobDropReport("1.0.1"), new JobDropReport("1.1.0")) < 0)
            .isTrue();
        assertThat(comparator.compare(new JobDropReport("2.0.0"), new JobDropReport("3.1")) < 0)
            .isTrue();
        assertThat(comparator.compare(new JobDropReport("1"), new JobDropReport("2")) < 0).isTrue();
        assertThat(comparator.compare(new JobDropReport("2"), new JobDropReport("1")) > 0).isTrue();
        assertThat(comparator.compare(new JobDropReport("1"), new JobDropReport("1")) == 0).isTrue();
        assertThat(comparator.compare(new JobDropReport("1"), new JobDropReport("1.1")) < 0).isTrue();
        assertThat(comparator.compare(new JobDropReport("1.1"), new JobDropReport("1")) > 0).isTrue();
        assertThat(comparator.compare(new JobDropReport("2"), new JobDropReport("1.1")) > 0).isTrue();
        assertThat(comparator.compare(new JobDropReport("1.1"), new JobDropReport("2")) < 0).isTrue();
        assertThat(comparator.compare(null, new JobDropReport("2.0.1")) < 0).isTrue();
        assertThat(comparator.compare(new JobDropReport("2.0.1"), null) == 1).isTrue();
        assertThat(comparator.compare(null, null) == 0).isTrue();
    }

    @Test
    public void testListOfDropNamesComparision() {

        List<JobDropReport> actualVersionsOrder = newArrayList(
            new JobDropReport("1"),
            new JobDropReport("1.1.1"),
            new JobDropReport("2.2"),
            new JobDropReport("1.11"),
            new JobDropReport("1.0"),
            new JobDropReport("2.11"),
            new JobDropReport("1.2"),
            new JobDropReport("2"),
            new JobDropReport("2.1"),
            new JobDropReport("2.12"),
                                                    new JobDropReport("1.1")
        );
        sort(actualVersionsOrder, comparator);

        assertThat(actualVersionsOrder)
            .containsSequence(
                new JobDropReport("1"),
                new JobDropReport("1.0"),
                new JobDropReport("1.1"),
                new JobDropReport("1.1.1"),
                new JobDropReport("1.2"),
                new JobDropReport("1.11"),
                new JobDropReport("2"),
                new JobDropReport("2.1"),
                new JobDropReport("2.2"),
                new JobDropReport("2.11"),
                new JobDropReport("2.12"));
    }
}

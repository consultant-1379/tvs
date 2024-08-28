package com.ericsson.gic.tms.tvs.domain.model.verdict.flakiness;

import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         20/12/2016
 */
public class FlakyTestCaseTest {

    private FlakyTestCase testCase = new FlakyTestCase();

    @Test
    public void getComponentsFlat() throws Exception {

        testCase.setComponents(newArrayList(
            newArrayList("a", "b", null),
            newArrayList("b", "c"),
            emptyList()));

        assertThat(testCase.getComponentsFlat()).containsOnly("a", "b", "c");
    }
}

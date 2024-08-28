package com.ericsson.gic.tms.tvs;

import com.ericsson.gic.tms.common.DateUtils;
import org.junit.Before;

import java.time.LocalDateTime;
import java.util.Date;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;

public abstract class AbstractDbunitTest extends AbstractIntegrationTest {

    protected Date startOfTest;

    @Before
    public void setUp() {
        startOfTest = new Date();
        mongoFixtures.dropCollection(JOB.getName());
        mongoFixtures.dropCollection(TEST_SESSION.getName());
        mongoFixtures.dropCollection(TEST_SUITE_RESULT.getName());
        mongoFixtures.dropCollection(TEST_CASE_RESULT.getName());
    }

    /**
     * Returns a date
     * from 1 hour before start of test,
     * after delta seconds
     */
    protected Date dateMoment(int delta) {
        return DateUtils.toDate(ldtMoment(delta));
    }

    private LocalDateTime ldtMoment(int delta) {
        return DateUtils.toLocalDateTime(startOfTest).minusHours(1).plusSeconds(delta);
    }
}

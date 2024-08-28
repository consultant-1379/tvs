package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.AggregationInfoBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RetryException;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RootControllerTest extends AbstractIntegrationTest {

    @Autowired
    private RootController rootController;

    @Test
    public void testAggregation() {
        AggregationInfoBean aggregationInfo = rootController.getAggregationInfo();
        assertThat(aggregationInfo).isNotNull();
        assertThat(aggregationInfo.getLastRun()).isNull();
        assertThat(aggregationInfo.isRunning()).isFalse();

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy());
        retryTemplate.setBackOffPolicy(new FixedBackOffPolicy());
        AggregationInfoBean aggregate = retryTemplate.execute(retryContext -> {
            AggregationInfoBean aggregateInfoBean = rootController.aggregate();

            if (aggregateInfoBean.getLastRun() == null) {
                throw new RetryException("Last run date is null");
            }

            return aggregateInfoBean;
        });
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getLastRun()).isBefore(LocalDateTime.now());
    }

}

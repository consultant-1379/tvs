package com.ericsson.gic.tms.tvs.presentation.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.*;
import static java.util.Collections.*;

public enum TestExecutionStatus {
    PASSED(bean -> bean.setPassed(bean.getPassed() + 1), unmodifiableList(asList("PASSED", "SUCCESS"))),
    FAILED(bean -> bean.setFailed(bean.getFailed() + 1), unmodifiableList(asList("FAILED", "FAILURE"))),
    CANCELLED(bean -> bean.setCancelled(bean.getCancelled() + 1),
        unmodifiableList(asList("CANCELED", "CANCELLED", "ABORTED"))),
    BROKEN(bean -> bean.setBroken(bean.getBroken() + 1), unmodifiableList(asList("BROKEN", "ERROR", "UNSTABLE"))),
    PENDING(bean -> bean.setPending(bean.getPending() + 1), unmodifiableList(singletonList("PENDING")));

    private Consumer<StatisticsBean> statisticsBeanConsumer;

    private List<String> aliases;

    TestExecutionStatus(Consumer<StatisticsBean> consumer, List<String> aliases) {
        this.statisticsBeanConsumer = consumer;
        this.aliases = aliases;
    }

    public void appendStatistics(StatisticsBean bean) {
        this.statisticsBeanConsumer.accept(bean);
    }

    public List<String> getAliases() {
        return aliases;
    }

    public static Optional<TestExecutionStatus> getByStatus(String status) {
        return Arrays.stream(values())
            .filter(testExecutionStatus -> testExecutionStatus.getAliases().contains(status))
            .findAny();
    }
}

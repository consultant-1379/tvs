package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultHistoryBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestCaseResultHistoryMapping implements CustomMapping<TestCaseResult, TestCaseResultHistoryBean> {

    @Override
    public ClassMapBuilder<TestCaseResult, TestCaseResultHistoryBean> map(
        ClassMapBuilder<TestCaseResult, TestCaseResultHistoryBean> classMapBuilder) {
        return classMapBuilder
            .field("executionId", "executionId");
    }
}

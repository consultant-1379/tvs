package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResultAggregate;
import com.ericsson.gic.tms.tvs.presentation.dto.TestVerdictBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestCaseResultAggregateMapping implements CustomMapping<TestCaseResultAggregate, TestVerdictBean> {

    @Override
    public ClassMapBuilder<TestCaseResultAggregate, TestVerdictBean> map(
        ClassMapBuilder<TestCaseResultAggregate, TestVerdictBean> classMapBuilder) {

        return classMapBuilder
            .field("testCaseId", "systemId");
    }
}

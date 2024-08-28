package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSuiteResultMapping implements CustomMapping<TestSuiteResult, TestSuiteResultBean> {

    @Override
    public ClassMapBuilder<TestSuiteResult, TestSuiteResultBean> map(
        ClassMapBuilder<TestSuiteResult, TestSuiteResultBean> classMapBuilder) {

        return classMapBuilder
            .exclude("id")
            .fieldAToB("createdDate", "createdDate");
    }
}

package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResultId;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseIdBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestCaseResultIdAggregateMapping implements CustomMapping<TestCaseResultId, TestCaseIdBean> {

    @Override
    public ClassMapBuilder<TestCaseResultId, TestCaseIdBean> map(
        ClassMapBuilder<TestCaseResultId, TestCaseIdBean> classMapBuilder) {

        return classMapBuilder
            .field("testCaseId", "systemId");
    }
}

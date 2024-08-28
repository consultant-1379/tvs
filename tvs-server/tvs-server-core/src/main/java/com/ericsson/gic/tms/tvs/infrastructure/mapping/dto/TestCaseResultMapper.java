package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultHistoryBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestCaseResultMapper implements DtoMapper<TestCaseResult, TestCaseResultHistoryBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestCaseResult toEntity(TestCaseResultHistoryBean dto) {
        return mapperFacade.map(dto, TestCaseResult.class);
    }

    @Override
    public TestCaseResultHistoryBean toDto(TestCaseResult entity) {
        return mapperFacade.map(entity, TestCaseResultHistoryBean.class);
    }

    @Override
    public void copyFields(TestCaseResultHistoryBean dto, TestCaseResult entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<TestCaseResultHistoryBean> mapAsList(Iterable<TestCaseResult> entities) {
        return mapperFacade.mapAsList(entities, TestCaseResultHistoryBean.class);
    }
}

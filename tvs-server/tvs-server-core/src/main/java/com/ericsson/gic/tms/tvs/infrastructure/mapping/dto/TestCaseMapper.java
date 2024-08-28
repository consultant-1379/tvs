package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestCaseMapper implements DtoMapper<TestCaseResult, TestCaseResultBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestCaseResult toEntity(TestCaseResultBean dto) {
        return mapperFacade.map(dto, TestCaseResult.class);
    }

    @Override
    public TestCaseResultBean toDto(TestCaseResult entity) {
        return mapperFacade.map(entity, TestCaseResultBean.class);
    }

    @Override
    public void copyFields(TestCaseResultBean dto, TestCaseResult entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<TestCaseResultBean> mapAsList(Iterable<TestCaseResult> entities) {
        return mapperFacade.mapAsList(entities, TestCaseResultBean.class);
    }
}

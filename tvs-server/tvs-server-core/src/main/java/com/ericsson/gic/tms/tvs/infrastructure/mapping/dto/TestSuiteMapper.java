package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSuiteResult;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSuiteResultBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestSuiteMapper implements DtoMapper<TestSuiteResult, TestSuiteResultBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestSuiteResult toEntity(TestSuiteResultBean dto) {
        return mapperFacade.map(dto, TestSuiteResult.class);
    }

    @Override
    public TestSuiteResultBean toDto(TestSuiteResult entity) {
        return mapperFacade.map(entity, TestSuiteResultBean.class);
    }

    @Override
    public void copyFields(TestSuiteResultBean dto, TestSuiteResult entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<TestSuiteResultBean> mapAsList(Iterable<TestSuiteResult> entities) {
        return mapperFacade.mapAsList(entities, TestSuiteResultBean.class);
    }
}

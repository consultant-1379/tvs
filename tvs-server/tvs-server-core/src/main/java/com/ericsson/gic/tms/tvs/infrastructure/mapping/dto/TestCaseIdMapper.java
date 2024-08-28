package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResultId;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseIdBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestCaseIdMapper implements DtoMapper<TestCaseResultId, TestCaseIdBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestCaseResultId toEntity(TestCaseIdBean dto) {
        return mapperFacade.map(dto, TestCaseResultId.class);
    }

    @Override
    public TestCaseIdBean toDto(TestCaseResultId entity) {
        return mapperFacade.map(entity, TestCaseIdBean.class);
    }

    @Override
    public void copyFields(TestCaseIdBean dto, TestCaseResultId entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<TestCaseIdBean> mapAsList(Iterable<TestCaseResultId> entities) {
        return mapperFacade.mapAsList(entities, TestCaseIdBean.class);
    }
}

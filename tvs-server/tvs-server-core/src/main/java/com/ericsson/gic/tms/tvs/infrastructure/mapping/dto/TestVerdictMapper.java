package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResultAggregate;
import com.ericsson.gic.tms.tvs.presentation.dto.TestVerdictBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestVerdictMapper implements DtoMapper<TestCaseResultAggregate, TestVerdictBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestCaseResultAggregate toEntity(TestVerdictBean dto) {
        return mapperFacade.map(dto, TestCaseResultAggregate.class);
    }

    @Override
    public TestVerdictBean toDto(TestCaseResultAggregate entity) {
        return mapperFacade.map(entity, TestVerdictBean.class);
    }

    @Override
    public void copyFields(TestVerdictBean dto, TestCaseResultAggregate entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<TestVerdictBean> mapAsList(Iterable<TestCaseResultAggregate> entities) {
        return mapperFacade.mapAsList(entities, TestVerdictBean.class);
    }
}

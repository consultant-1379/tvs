package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestSessionMapper implements DtoMapper<TestSession, TestSessionBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestSession toEntity(TestSessionBean dto) {
        return mapperFacade.map(dto, TestSession.class);
    }

    @Override
    public TestSessionBean toDto(TestSession entity) {
        return mapperFacade.map(entity, TestSessionBean.class);
    }

    @Override
    public void copyFields(TestSessionBean dto, TestSession entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<TestSessionBean> mapAsList(Iterable<TestSession> entities) {
        return mapperFacade.mapAsList(entities, TestSessionBean.class);
    }
}

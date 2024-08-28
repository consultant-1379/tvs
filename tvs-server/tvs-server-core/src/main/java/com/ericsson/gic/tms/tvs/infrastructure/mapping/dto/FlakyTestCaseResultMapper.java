package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.flakiness.FlakyTestCaseResult;
import com.ericsson.gic.tms.tvs.presentation.dto.flakiness.FlakyTestCaseResultBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlakyTestCaseResultMapper implements DtoMapper<FlakyTestCaseResult, FlakyTestCaseResultBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public FlakyTestCaseResult toEntity(FlakyTestCaseResultBean dto) {
        return mapperFacade.map(dto, FlakyTestCaseResult.class);
    }

    @Override
    public FlakyTestCaseResultBean toDto(FlakyTestCaseResult entity) {
        return mapperFacade.map(entity, FlakyTestCaseResultBean.class);
    }

    @Override
    public void copyFields(FlakyTestCaseResultBean dto, FlakyTestCaseResult entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<FlakyTestCaseResultBean> mapAsList(Iterable<FlakyTestCaseResult> entities) {
        return mapperFacade.mapAsList(entities, FlakyTestCaseResultBean.class);
    }
}

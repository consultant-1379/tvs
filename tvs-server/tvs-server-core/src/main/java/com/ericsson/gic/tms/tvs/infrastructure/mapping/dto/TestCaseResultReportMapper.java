package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultReport;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestCaseResultReportMapper implements DtoMapper<TestCaseResult, TestCaseResultReport> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestCaseResult toEntity(TestCaseResultReport dto) {
        return mapperFacade.map(dto, TestCaseResult.class);
    }

    @Override
    public TestCaseResultReport toDto(TestCaseResult entity) {
        return mapperFacade.map(entity, TestCaseResultReport.class);
    }

    @Override
    public void copyFields(TestCaseResultReport dto, TestCaseResult entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<TestCaseResultReport> mapAsList(Iterable<TestCaseResult> entities) {
        return mapperFacade.mapAsList(entities, TestCaseResultReport.class);
    }
}

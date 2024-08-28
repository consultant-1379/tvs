package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseReport;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestCaseReportMapper implements DtoMapper<TestCaseResult, TestCaseReport> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestCaseResult toEntity(TestCaseReport dto) {
        return mapperFacade.map(dto, TestCaseResult.class);
    }

    @Override
    public TestCaseReport toDto(TestCaseResult entity) {
        return mapperFacade.map(entity, TestCaseReport.class);
    }

    @Override
    public void copyFields(TestCaseReport dto, TestCaseResult entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<TestCaseReport> mapAsList(Iterable<TestCaseResult> entities) {
        return mapperFacade.mapAsList(entities, TestCaseReport.class);
    }
}

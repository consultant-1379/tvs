package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.presentation.dto.JobExecutionReport;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobExecutionReportMapper implements DtoMapper<TestSession, JobExecutionReport> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public TestSession toEntity(JobExecutionReport dto) {
        return mapperFacade.map(dto, TestSession.class);
    }

    @Override
    public JobExecutionReport toDto(TestSession entity) {
        return mapperFacade.map(entity, JobExecutionReport.class);
    }

    @Override
    public void copyFields(JobExecutionReport dto, TestSession entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<JobExecutionReport> mapAsList(Iterable<TestSession> entities) {
        return mapperFacade.mapAsList(entities, JobExecutionReport.class);
    }
}

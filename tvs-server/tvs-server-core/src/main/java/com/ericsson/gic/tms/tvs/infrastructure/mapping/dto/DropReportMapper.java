package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop.JobDropReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.DropReportBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DropReportMapper implements DtoMapper<JobDropReport, DropReportBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public JobDropReport toEntity(DropReportBean dto) {
        return mapperFacade.map(dto, JobDropReport.class);
    }

    @Override
    public DropReportBean toDto(JobDropReport entity) {
        return mapperFacade.map(entity, DropReportBean.class);
    }

    @Override
    public void copyFields(DropReportBean dto, JobDropReport entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<DropReportBean> mapAsList(Iterable<JobDropReport> entities) {
        return mapperFacade.mapAsList(entities, DropReportBean.class);
    }
}

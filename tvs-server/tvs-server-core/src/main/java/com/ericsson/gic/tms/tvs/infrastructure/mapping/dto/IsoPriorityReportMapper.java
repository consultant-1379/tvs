package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.IsoPriorityReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoPriorityReportBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IsoPriorityReportMapper implements DtoMapper<IsoPriorityReport, IsoPriorityReportBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public IsoPriorityReport toEntity(IsoPriorityReportBean dto) {
        return mapperFacade.map(dto, IsoPriorityReport.class);
    }

    @Override
    public IsoPriorityReportBean toDto(IsoPriorityReport entity) {
        return mapperFacade.map(entity, IsoPriorityReportBean.class);
    }

    @Override
    public void copyFields(IsoPriorityReportBean dto, IsoPriorityReport entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<IsoPriorityReportBean> mapAsList(Iterable<IsoPriorityReport> entities) {
        return mapperFacade.mapAsList(entities, IsoPriorityReportBean.class);
    }
}

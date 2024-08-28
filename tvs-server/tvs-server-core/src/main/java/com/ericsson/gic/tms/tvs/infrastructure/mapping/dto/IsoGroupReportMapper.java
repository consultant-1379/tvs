package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.IsoGroupReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoGroupReportBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IsoGroupReportMapper implements DtoMapper<IsoGroupReport, IsoGroupReportBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public IsoGroupReport toEntity(IsoGroupReportBean dto) {
        return mapperFacade.map(dto, IsoGroupReport.class);
    }

    @Override
    public IsoGroupReportBean toDto(IsoGroupReport entity) {
        return mapperFacade.map(entity, IsoGroupReportBean.class);
    }

    @Override
    public void copyFields(IsoGroupReportBean dto, IsoGroupReport entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<IsoGroupReportBean> mapAsList(Iterable<IsoGroupReport> entities) {
        return mapperFacade.mapAsList(entities, IsoGroupReportBean.class);
    }
}

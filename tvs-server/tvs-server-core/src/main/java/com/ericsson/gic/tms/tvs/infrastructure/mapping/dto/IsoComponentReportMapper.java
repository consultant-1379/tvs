package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.IsoComponentReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoComponentReportBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IsoComponentReportMapper implements DtoMapper<IsoComponentReport, IsoComponentReportBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public IsoComponentReport toEntity(IsoComponentReportBean dto) {
        return mapperFacade.map(dto, IsoComponentReport.class);
    }

    @Override
    public IsoComponentReportBean toDto(IsoComponentReport entity) {
        return mapperFacade.map(entity, IsoComponentReportBean.class);
    }

    @Override
    public void copyFields(IsoComponentReportBean dto, IsoComponentReport entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<IsoComponentReportBean> mapAsList(Iterable<IsoComponentReport> entities) {
        return mapperFacade.mapAsList(entities, IsoComponentReportBean.class);
    }
}

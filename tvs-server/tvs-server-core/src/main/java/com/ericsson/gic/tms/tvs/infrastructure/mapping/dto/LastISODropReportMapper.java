package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop.JobLastISODropReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.JobLastISODropReportBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LastISODropReportMapper implements DtoMapper<JobLastISODropReport, JobLastISODropReportBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public JobLastISODropReport toEntity(JobLastISODropReportBean dto) {
        return mapperFacade.map(dto, JobLastISODropReport.class);
    }

    @Override
    public JobLastISODropReportBean toDto(JobLastISODropReport entity) {
        return mapperFacade.map(entity, JobLastISODropReportBean.class);
    }

    @Override
    public void copyFields(JobLastISODropReportBean dto, JobLastISODropReport entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<JobLastISODropReportBean> mapAsList(Iterable<JobLastISODropReport> entities) {
        return mapperFacade.mapAsList(entities, JobLastISODropReportBean.class);
    }
}

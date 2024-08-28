package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop.JobCumulativeDropReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.drop.JobCumulativeDropReportBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CumulativeDropReportMapper implements DtoMapper<JobCumulativeDropReport, JobCumulativeDropReportBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public JobCumulativeDropReport toEntity(JobCumulativeDropReportBean dto) {
        return mapperFacade.map(dto, JobCumulativeDropReport.class);
    }

    @Override
    public JobCumulativeDropReportBean toDto(JobCumulativeDropReport entity) {
        return mapperFacade.map(entity, JobCumulativeDropReportBean.class);
    }

    @Override
    public void copyFields(JobCumulativeDropReportBean dto, JobCumulativeDropReport entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<JobCumulativeDropReportBean> mapAsList(Iterable<JobCumulativeDropReport> entities) {
        return mapperFacade.mapAsList(entities, JobCumulativeDropReportBean.class);
    }
}

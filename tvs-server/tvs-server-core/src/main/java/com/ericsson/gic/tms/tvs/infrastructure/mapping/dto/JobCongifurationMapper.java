package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.JobConfiguration;
import com.ericsson.gic.tms.tvs.presentation.dto.JobConfigurationBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class JobCongifurationMapper implements DtoMapper<JobConfiguration, JobConfigurationBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public JobConfiguration toEntity(JobConfigurationBean dto) {
        return mapperFacade.map(dto, JobConfiguration.class);
    }

    @Override
    public JobConfigurationBean toDto(JobConfiguration entity) {
        return mapperFacade.map(entity, JobConfigurationBean.class);
    }

    @Override
    public void copyFields(JobConfigurationBean dto, JobConfiguration entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<JobConfigurationBean> mapAsList(Iterable<JobConfiguration> entities) {
        return mapperFacade.mapAsList(entities, JobConfigurationBean.class);
    }
}

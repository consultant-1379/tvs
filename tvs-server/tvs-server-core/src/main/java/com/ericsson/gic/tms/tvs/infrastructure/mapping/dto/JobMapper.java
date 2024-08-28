package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobMapper implements DtoMapper<Job, JobBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public Job toEntity(JobBean dto) {
        return mapperFacade.map(dto, Job.class);
    }

    @Override
    public JobBean toDto(Job entity) {
        return mapperFacade.map(entity, JobBean.class);
    }

    @Override
    public void copyFields(JobBean dto, Job entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<JobBean> mapAsList(Iterable<Job> entities) {
        return mapperFacade.mapAsList(entities, JobBean.class);
    }
}

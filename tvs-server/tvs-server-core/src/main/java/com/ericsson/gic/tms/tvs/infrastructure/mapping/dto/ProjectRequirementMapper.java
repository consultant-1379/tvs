package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.presentation.dto.ProjectRequirementBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ProjectRequirementMapper implements DtoMapper<ProjectRequirement, ProjectRequirementBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public ProjectRequirement toEntity(ProjectRequirementBean dto) {
        return mapperFacade.map(dto, ProjectRequirement.class);
    }

    @Override
    public ProjectRequirementBean toDto(ProjectRequirement entity) {
        return mapperFacade.map(entity, ProjectRequirementBean.class);
    }

    @Override
    public void copyFields(ProjectRequirementBean dto, ProjectRequirement entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<ProjectRequirementBean> mapAsList(Iterable<ProjectRequirement> entities) {
        return mapperFacade.mapAsList(entities, ProjectRequirementBean.class);
    }
}

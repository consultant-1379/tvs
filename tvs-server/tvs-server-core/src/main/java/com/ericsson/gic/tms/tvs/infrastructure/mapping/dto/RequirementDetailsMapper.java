package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementDetailsBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RequirementDetailsMapper implements DtoMapper<ProjectRequirement, RequirementDetailsBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public ProjectRequirement toEntity(RequirementDetailsBean dto) {
        return mapperFacade.map(dto, ProjectRequirement.class);
    }

    @Override
    public RequirementDetailsBean toDto(ProjectRequirement entity) {
        return mapperFacade.map(entity, RequirementDetailsBean.class);
    }

    @Override
    public void copyFields(RequirementDetailsBean dto, ProjectRequirement entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<RequirementDetailsBean> mapAsList(Iterable<ProjectRequirement> entities) {
        return mapperFacade.mapAsList(entities, RequirementDetailsBean.class);
    }
}

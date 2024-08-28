package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementReport;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequirementReportMapper implements DtoMapper<ProjectRequirement, RequirementReport> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public ProjectRequirement toEntity(RequirementReport dto) {
        return mapperFacade.map(dto, ProjectRequirement.class);
    }

    @Override
    public RequirementReport toDto(ProjectRequirement entity) {
        return mapperFacade.map(entity, RequirementReport.class);
    }

    @Override
    public void copyFields(RequirementReport dto, ProjectRequirement entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<RequirementReport> mapAsList(Iterable<ProjectRequirement> entities) {
        return mapperFacade.mapAsList(entities, RequirementReport.class);
    }
}

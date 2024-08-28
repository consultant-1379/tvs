package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.presentation.dto.ProjectRequirementBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectRequirementMapping implements CustomMapping<ProjectRequirement, ProjectRequirementBean> {

    @Override
    public ClassMapBuilder<ProjectRequirement, ProjectRequirementBean> map(
        ClassMapBuilder<ProjectRequirement, ProjectRequirementBean> classMapBuilder) {

        return classMapBuilder
            .exclude("children")
            .exclude("additionalFields");
    }

    /**
     * Due to a strange Orika behaviour in case of handling collection as an additional field value data can be lost
     */
    @Override
    public void customBtoA(ProjectRequirementBean from, ProjectRequirement to) {
        to.setAdditionalFields(from.getAdditionalFields());
    }

    @Override
    public void customAtoB(ProjectRequirement from, ProjectRequirementBean to) {
        to.setAdditionalFields(from.getAdditionalFields());
    }

}

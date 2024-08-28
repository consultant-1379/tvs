package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementDetailsBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequirementDetailsMapping implements CustomMapping<ProjectRequirement, RequirementDetailsBean> {

    @Override
    public ClassMapBuilder<ProjectRequirement, RequirementDetailsBean> map(
        ClassMapBuilder<ProjectRequirement, RequirementDetailsBean> classMapBuilder) {

        return classMapBuilder
            .exclude("children")
            .exclude("additionalFields");
    }

    /**
     * Due to a strange Orika behaviour in case of handling collection as an additional field value data can be lost
     */
    @Override
    public void customBtoA(RequirementDetailsBean from, ProjectRequirement to) {
        to.setAdditionalFields(from.getAdditionalFields());
    }

    @Override
    public void customAtoB(ProjectRequirement from, RequirementDetailsBean to) {
        to.setAdditionalFields(from.getAdditionalFields());
    }
}

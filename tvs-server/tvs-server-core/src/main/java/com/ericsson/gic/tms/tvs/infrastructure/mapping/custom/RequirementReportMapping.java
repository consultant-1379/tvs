package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementReport;
import com.google.common.base.Optional;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;

@Configuration
public class RequirementReportMapping implements CustomMapping<ProjectRequirement, RequirementReport> {

    @Override
    public ClassMapBuilder<ProjectRequirement, RequirementReport> map(
        ClassMapBuilder<ProjectRequirement, RequirementReport> classMapBuilder) {

        return classMapBuilder
            .exclude("children")
            .exclude("additionalFields");
    }

    @Override
    public void customAtoB(ProjectRequirement from, RequirementReport to) {
        to.setEpicCount((Integer) Optional.fromNullable(from.getAdditionalField(EPIC_COUNT)).or(0));
        to.setTestCaseCount((Integer) Optional.fromNullable(from.getAdditionalField(TEST_CASE_COUNT)).or(0));
        to.setUserStoryCount((Integer) Optional.fromNullable(from.getAdditionalField(USER_STORY_COUNT)).or(0));
        to.setUserStoryWithResults((Integer) Optional.fromNullable(from.getAdditionalField(US_WITH_TEST_RESULT_COUNT))
            .or(0));
        to.setSoc((Double) Optional.fromNullable(from.getAdditionalField(SOC)).or(0.0));
        to.setSov((Double) Optional.fromNullable(from.getAdditionalField(SOV)).or(0.0));
    }
}

package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.presentation.dto.JobReport;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobReportMapping implements CustomMapping<Job, JobReport> {

    @Override
    public ClassMapBuilder<Job, JobReport> map(
        ClassMapBuilder<Job, JobReport> classMapBuilder) {

        return classMapBuilder
            .fieldAToB("uid", "id")
            .exclude("additionalFields");
    }
}

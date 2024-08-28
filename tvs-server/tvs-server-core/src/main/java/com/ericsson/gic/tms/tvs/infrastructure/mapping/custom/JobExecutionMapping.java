package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.presentation.dto.JobExecutionReport;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;

@Configuration
public class JobExecutionMapping implements CustomMapping<TestSession, JobExecutionReport> {

    @Override
    public ClassMapBuilder<TestSession, JobExecutionReport> map(
        ClassMapBuilder<TestSession, JobExecutionReport> classMapBuilder) {

        return classMapBuilder
            .field("executionId", "id")
            .field("time.startDate", "startDate")
            .field("time.stopDate", "stopDate")
            .field("time.duration", "duration")
            .exclude("additionalFields");
    }

    @Override
    public void customAtoB(TestSession from, JobExecutionReport to) {
        to.setDropName((String) from.getAdditionalField(DROP_NAME));
        to.setIsoVersion((String) from.getAdditionalField(ISO_VERSION));
        to.setIsoArtifact((String) from.getAdditionalField(ISO_ARTIFACT_ID));
        to.setJobName((String) from.getAdditionalField(JENKINS_JOB_NAME));
    }
}

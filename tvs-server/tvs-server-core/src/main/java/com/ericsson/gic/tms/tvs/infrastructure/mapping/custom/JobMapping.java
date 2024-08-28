package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobMapping implements CustomMapping<Job, JobBean> {

    @Override
    public ClassMapBuilder<Job, JobBean> map(
        ClassMapBuilder<Job, JobBean> classMapBuilder) {

        return classMapBuilder
            .fieldAToB("contextId", "context")
            .fieldAToB("uid", "id")
            .exclude("additionalFields");
    }

    /**
     * Due to a strange Orika behaviour in case of handling collection as an additional field value data can be lost
     */
    @Override
    public void customBtoA(JobBean from, Job to) {
        to.setAdditionalFields(from.getAdditionalFields());
    }

    @Override
    public void customAtoB(Job from, JobBean to) {
        to.setAdditionalFields(from.getAdditionalFields());
    }
}

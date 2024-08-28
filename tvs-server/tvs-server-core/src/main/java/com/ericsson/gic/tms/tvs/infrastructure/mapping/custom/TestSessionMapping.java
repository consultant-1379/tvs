package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSessionMapping implements CustomMapping<TestSession, TestSessionBean> {

    @Override
    public ClassMapBuilder<TestSession, TestSessionBean> map(
        ClassMapBuilder<TestSession, TestSessionBean> classMapBuilder) {

        return classMapBuilder
            .fieldAToB("createdDate", "createdDate")
            .exclude("additionalFields")
            .exclude("id")
            .mapNullsInReverse(false);
    }

    /**
     * Due to a strange Orika behaviour in case of handling collection as an additional field value data can be lost
     */
    @Override
    public void customAtoB(TestSession from, TestSessionBean to) {
        to.setAdditionalFields(from.getAdditionalFields());
    }

    @Override
    public void customBtoA(TestSessionBean from, TestSession to) {
        if (!from.getAdditionalFields().isEmpty()) {
            to.setAdditionalFields(from.getAdditionalFields());
        }
    }
}

package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.notification.Criteria;
import com.ericsson.gic.tms.tvs.presentation.dto.RuleBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CriteriaMapping implements CustomMapping<Criteria, RuleBean> {

    @Override
    public ClassMapBuilder<Criteria, RuleBean> map(
        ClassMapBuilder<Criteria, RuleBean> classMapBuilder) {

        return classMapBuilder.byDefault();
    }

    @Override
    public void customAtoB(Criteria from, RuleBean to) {
        from.setValue(to.getValue());
    }

    @Override
    public void customBtoA(RuleBean from, Criteria to) {
        to.setValue(from.getValue());
    }
}

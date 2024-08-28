package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.DtoMapper;
import com.ericsson.gic.tms.tvs.domain.model.verdict.notification.Criteria;
import com.ericsson.gic.tms.tvs.presentation.dto.RuleBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CriteriaMapper implements DtoMapper<Criteria, RuleBean> {

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public Criteria toEntity(RuleBean dto) {
        return mapperFacade.map(dto, Criteria.class);
    }

    @Override
    public RuleBean toDto(Criteria entity) {
        return mapperFacade.map(entity, RuleBean.class);
    }

    @Override
    public void copyFields(RuleBean dto, Criteria entity) {
        mapperFacade.map(dto, entity);
    }

    @Override
    public List<RuleBean> mapAsList(Iterable<Criteria> entities) {
        return mapperFacade.mapAsList(entities, RuleBean.class);
    }
}

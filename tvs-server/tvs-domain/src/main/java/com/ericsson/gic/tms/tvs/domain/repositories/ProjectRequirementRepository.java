package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirementType;
import com.ericsson.gic.tms.tvs.domain.util.EqualsFilter;
import com.ericsson.gic.tms.tvs.domain.util.InFilter;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.google.common.collect.Lists.*;

@Repository
public class ProjectRequirementRepository extends BaseJongoRepository<ProjectRequirement, String> {

    public ProjectRequirementRepository() {
        super(ProjectRequirement.class);
    }

    @Override
    protected String getCollectionName() {
        return PROJECT_REQUIREMENT.getName();
    }

    public void saveAdditional(String id, Map<String, Object> additionalFields) {
        ProjectRequirement one = findOne(id);

        if (one == null) {
            return;
        }

        additionalFields.entrySet().stream()
            .forEach(entry -> one.addAdditionalFields(entry.getKey(), entry.getValue()));

        save(one);
    }

    public List<ProjectRequirement> findByType(ProjectRequirementType type) {
        return newArrayList(findBy(new Query(new EqualsFilter("type", type.name()))));
    }

    public Page<ProjectRequirement> findByType(ProjectRequirementType type, PageRequest pageRequest, Query query) {
        Query filters = cloneQuery(query)
            .addFilter(new EqualsFilter("type", type.name()));
        return findBy(filters, pageRequest);
    }

    public Page<ProjectRequirement> findByIdIn(List<String> requirementIds, PageRequest pageRequest, Query query) {
        Query filters = cloneQuery(query)
            .addFilter(new InFilter("_id", requirementIds));
        return findBy(filters, pageRequest);
    }

    public List<ProjectRequirement> findByIdIn(List<String> requirementIds) {
        return newArrayList(findBy(new Query(new InFilter("_id", requirementIds))));
    }
}

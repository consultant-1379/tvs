package com.ericsson.gic.tms.tvs.domain.model.verdict;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import com.google.common.collect.Lists;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class ProjectRequirement extends AdditionalFieldAware implements MongoEntity<String>, AuditAware  {

    @MongoId
    private String id;

    private String name;

    private ProjectRequirementType type;

    private String contextId;

    private List<String> children = Lists.newArrayList();

    private Date modifiedDate;

    private Date createdDate;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public void addChildren(ProjectRequirement... child) {
        Stream.of(child)
            .map(ProjectRequirement::getId)
            .forEach(children::add);
    }

    public ProjectRequirementType getType() {
        return type;
    }

    public void setType(ProjectRequirementType type) {
        this.type = type;
    }

    @Override
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}

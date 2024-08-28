package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlRootElement
public class ProjectRequirementBean extends AdditionalFieldAware implements Attributes<String> {

    @NotEmpty
    private String id;

    private String name;

    @NotNull
    private String contextId;

    @NotNull
    private RequirementType type;

    @Valid
    @NotNull
    private List<ProjectRequirementBean> children = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

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
        children.forEach(child -> child.setContextId(contextId));
    }

    public RequirementType getType() {
        return type;
    }

    public void setType(RequirementType type) {
        this.type = type;
    }

    public List<ProjectRequirementBean> getChildren() {
        return children;
    }

    public void setChildren(List<ProjectRequirementBean> children) {
        this.children = children;
    }

    public void addChildren(ProjectRequirementBean ... childArray) {
        this.children.addAll(Arrays.asList(childArray));
    }

    public void addChild(ProjectRequirementBean child) {
        this.children.add(child);
    }
}

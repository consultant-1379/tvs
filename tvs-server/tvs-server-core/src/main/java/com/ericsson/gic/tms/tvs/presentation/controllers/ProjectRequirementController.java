package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.ProjectRequirementService;
import com.ericsson.gic.tms.tvs.application.services.QueryService;
import com.ericsson.gic.tms.tvs.application.services.SortService;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.presentation.dto.ProjectRequirementBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementDetailsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementReport;
import com.ericsson.gic.tms.tvs.presentation.resources.ProjectRequirementResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import static javax.ws.rs.core.HttpHeaders.*;

@Controller
public class ProjectRequirementController
    extends AbstractJsonApiCapableController
    implements ProjectRequirementResource {

    private static final String REQUIREMENTS_CSV_FILE_NAME = "inline; filename=\"requirements.csv\"";
    private static final String EPICS_CSV_FILE_NAME = "inline; filename=\"%s-requirement-epics.csv\"";

    @Autowired
    private ProjectRequirementService service;

    @Autowired
    private QueryService queryService;

    @Autowired
    private SortService sortService;

    @Override
    public Document<ProjectRequirementBean> save(ProjectRequirementBean bean) {
        return responseFor(service.save(bean))
            .withSelfRel(ProjectRequirementResource.class)
            .build();
    }

    @Override
    public DocumentList<RequirementDetailsBean> getRequirements(String requirementId, int page, int size,
                                                                String orderBy, SortingMode orderMode, String query) {
        PageRequest pageRequest = new PageRequest(page - 1, size, sortService.getSort(orderMode.toString(), orderBy));
        Query queryObj = queryService.createQuery(query);

        Page<RequirementDetailsBean> result = service.findAllBy(requirementId, pageRequest, queryObj);

        return responseFor(result.getContent())
            .withSelfRel(ProjectRequirementResource.class, REQUIREMENT_CHILDREN)
            .withPagination(ProjectRequirementResource.class, result, pageRequest)
            .build();
    }

    @Override
    public DocumentList<RequirementReport> getRequirements(String requirementId) {
        return responseFor(service.findAllBy(requirementId))
            .withSelfRel(ProjectRequirementResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(EPICS_CSV_FILE_NAME, requirementId))
            .build();
    }

    @Override
    public DocumentList<RequirementDetailsBean> getRequirements(int page, int size, String orderBy,
                                                                SortingMode orderMode, String query) {
        PageRequest pageRequest = new PageRequest(page - 1, size, sortService.getSort(orderMode.toString(), orderBy));
        Query queryObj = queryService.createQuery(query);

        Page<RequirementDetailsBean> result = service.findAll(pageRequest, queryObj);

        return responseFor(result.getContent())
            .withSelfRel(ProjectRequirementResource.class)
            .withPagination(ProjectRequirementResource.class, result, pageRequest)
            .build();
    }

    @Override
    public DocumentList<RequirementReport> getRequirements() {
        return responseFor(service.findAll())
            .withSelfRel(ProjectRequirementResource.class)
            .withMeta(CONTENT_DISPOSITION, REQUIREMENTS_CSV_FILE_NAME)
            .build();
    }

    @Override
    public Document<RequirementDetailsBean> getRequirement(String requirementId) {
        return responseFor(service.findById(requirementId))
            .withSelfRel(ProjectRequirementResource.class, REQUIREMENT_ID)
            .build();
    }
}

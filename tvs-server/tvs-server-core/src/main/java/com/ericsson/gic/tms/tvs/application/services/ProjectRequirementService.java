package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.domain.repositories.ProjectRequirementRepository;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.ProjectRequirementMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.RequirementDetailsMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.RequirementReportMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.ProjectRequirementBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementDetailsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.*;
import static com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirementType.*;
import static java.util.stream.Collectors.*;

@Service
public class ProjectRequirementService {

    @Autowired
    private ProjectRequirementRepository projectRequirementRepo;

    @Autowired
    private ProjectRequirementMapper requirementMapper;

    @Autowired
    private RequirementDetailsMapper requirementDetailsMapper;

    @Autowired
    private RequirementReportMapper requirementReportMapper;

    public ProjectRequirementBean save(ProjectRequirementBean bean) {
        List<String> childIds = bean.getChildren().stream()
            .map(this::save)
            .map(ProjectRequirementBean::getId)
            .collect(toList());

        ProjectRequirement mapped = requirementMapper.toEntity(bean);
        mapped.setChildren(childIds);

        ProjectRequirement requirement = projectRequirementRepo.save(mapped);
        return requirementMapper.toDto(requirement);
    }


    public Page<RequirementDetailsBean> findAllBy(String requirementId, PageRequest pageRequest, Query query) {
        ProjectRequirement requirement = verifyFound(projectRequirementRepo.findOne(requirementId));
        Page<ProjectRequirement> foundRequirements = projectRequirementRepo
            .findByIdIn(requirement.getChildren(), pageRequest, query);
        return requirementDetailsMapper.mapAsPage(foundRequirements, pageRequest);
    }

    public List<RequirementReport> findAllBy(String requirementId) {
        ProjectRequirement requirement = verifyFound(projectRequirementRepo.findOne(requirementId));
        List<ProjectRequirement> foundRequirements = projectRequirementRepo.findByIdIn(requirement.getChildren());
        return requirementReportMapper.mapAsList(foundRequirements);
    }

    public Page<RequirementDetailsBean> findAll(PageRequest pageRequest, Query query) {
        Page<ProjectRequirement> foundRequirements = projectRequirementRepo.findByType(MR, pageRequest, query);
        return requirementDetailsMapper.mapAsPage(foundRequirements, pageRequest);
    }

    public List<RequirementReport> findAll() {
        List<ProjectRequirement> foundRequirements = projectRequirementRepo.findByType(MR);
        return requirementReportMapper.mapAsList(foundRequirements);
    }

    public RequirementDetailsBean findById(String requirementId) {
        ProjectRequirement requirement = verifyFound(projectRequirementRepo.findOne(requirementId));
        return requirementDetailsMapper.toDto(requirement);
    }
}

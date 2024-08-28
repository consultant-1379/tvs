package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.ProjectRequirementBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementDetailsBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RequirementType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.*;
import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.RequirementType.*;
import static org.assertj.core.api.Assertions.*;

public class ProjectRequirementControllerTest extends AbstractIntegrationTest {

    private static final int PAGE = 1;
    private static final int SIZE = 20;
    private static final String PROJECT_ID = "DURACI";
    private static final String CONTEXT_ID = "systemId-1";
    private static final String MR_ID_1 = "MR1";
    private static final String MR_ID_2 = "MR2";
    private static final String EPIC_ID_1 = "EPIC1";
    private static final String EPIC_ID_2 = "EPIC2";
    private static final String STORY_ID_1 = "US1";
    private static final String STORY_ID_2 = "US2";
    private static final String ORDER_BY = "id";

    @Autowired
    private ProjectRequirementController controller;

    private ProjectRequirementBean projectRequirementBean;

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);
        projectRequirementBean = new ProjectRequirementBean();
        projectRequirementBean.setId(PROJECT_ID);
        projectRequirementBean.setName("DURA CI");
        projectRequirementBean.setType(PROJECT);
        projectRequirementBean.setContextId(CONTEXT_ID);
    }

    @Test
    public void updateSingleRequirement() {
        ProjectRequirementBean response1 = controller.save(projectRequirementBean).unwrap();
        assertThat(response1)
            .as("name of Requirement Nr1")
            .isEqualToComparingFieldByField(projectRequirementBean);

        // update reset name
        projectRequirementBean.setName("DURA CI 2");
        ProjectRequirementBean response2 = controller.save(projectRequirementBean).unwrap();

        assertThat(response2.getName())
            .as("name of Requirement Nr2")
            .isEqualTo(projectRequirementBean.getName());
    }

    @Test
    public void saveSingleRequirement() {
        Document<ProjectRequirementBean> response = controller.save(projectRequirementBean);

        assertThat(response).as("Response").isNotNull();
        assertThat(response.getErrors()).as("Response errors").isNullOrEmpty();
        assertThat(response.getMeta()).as("Response meta").isNull();
        assertThat(response.unwrap())
            .as("Saved project requirement")
            .isEqualToComparingFieldByField(projectRequirementBean);
    }

    @Test
    public void saveMainRequirementTree() {
        ProjectRequirementBean mainRequirement = createRequirement("TORF-42687", "Treat as support in ENM", MR);
        projectRequirementBean.addChild(mainRequirement);

        Document<ProjectRequirementBean> response = controller.save(projectRequirementBean);
        assertThat(response)
            .as("Saved requirement response")
            .isNotNull();

        assertThat(response.getData())
            .as("Saved requirement response data")
            .isNotNull();

        assertRequirement(PROJECT_ID, projectRequirementBean);
        assertRequirement(mainRequirement.getId(), mainRequirement);
    }

    @Test
    public void saveEpicRequirementTree() {
        ProjectRequirementBean epicRequirement = createRequirement("TORF-102000", "Auto Provisioning epic", EPIC);
        ProjectRequirementBean mainRequirement = createRequirement("TORF-42687", "Treat as support in ENM", MR);
        mainRequirement.addChild(epicRequirement);
        projectRequirementBean.addChild(mainRequirement);

        Document<ProjectRequirementBean> response = controller.save(projectRequirementBean);
        assertThat(response)
            .as("Saved requirement response")
            .isNotNull();

        assertRequirement(PROJECT_ID, projectRequirementBean);
        assertRequirement(mainRequirement.getId(), mainRequirement);
        assertRequirement(epicRequirement.getId(), epicRequirement);
    }

    @Test
    public void saveStoriesTree() {
        ProjectRequirementBean story1 = createRequirement("TORF-93661", "OSS target model identity", STORY);
        ProjectRequirementBean story2 = createRequirement("TORF-102004", "Verify certificate", STORY);
        ProjectRequirementBean epicRequirement = createRequirement("TORF-102000", "Auto Provisioning epic", EPIC);
        ProjectRequirementBean mainRequirement = createRequirement("TORF-42687", "Treat as support in ENM", MR);

        epicRequirement.addChildren(story1, story2);
        mainRequirement.addChild(epicRequirement);
        projectRequirementBean.addChild(mainRequirement);

        Document<ProjectRequirementBean> response = controller.save(projectRequirementBean);
        assertThat(response)
            .as("Saved requirement response")
            .isNotNull();

        assertRequirement(PROJECT_ID, projectRequirementBean);
        assertRequirement(mainRequirement.getId(), mainRequirement);
        assertRequirement(epicRequirement.getId(), epicRequirement);
        assertRequirement(story1.getId(), story1);
        assertRequirement(story2.getId(), story2);
    }

    @Test
    public void saveRequirementWithoutId() {
        projectRequirementBean.setId(null);

        assertThatThrownBy(() -> controller.save(projectRequirementBean))
            .as("Save requirement with null ID")
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void saveRequirementWithoutType() {
        projectRequirementBean.setType(null);
        assertThatThrownBy(() -> controller.save(projectRequirementBean))
            .as("Save requirement with null type")
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void saveRequirementWithNullChildren() {
        projectRequirementBean.setChildren(null);
        assertThatThrownBy(() -> controller.save(projectRequirementBean))
            .as("Save requirement with null children")
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void getAllRequirements() {
        String query = null;
        DocumentList<RequirementDetailsBean> response = controller.getRequirements(PAGE, SIZE,
            ORDER_BY, ASC, query);
        assertPageResponse(response, 2L);

        List<RequirementDetailsBean> requirements = response.unwrap();
        assertThat(requirements)
            .as("Requirements list")
            .isNotEmpty();

        assertThat(requirements)
            .as("Requirement types")
            .extracting(RequirementDetailsBean::getType).contains(MR);

        assertThat(requirements)
            .as("Requirement IDs")
            .extracting(RequirementDetailsBean::getId).contains(MR_ID_1, MR_ID_2);
    }

    @Test
    public void getMainRequirements() {
        String query = null;
        DocumentList<RequirementDetailsBean> response = controller.getRequirements(MR_ID_1, PAGE, SIZE, ORDER_BY, ASC,
            query);
        assertPageResponse(response, 2L);

        List<RequirementDetailsBean> requirements = response.unwrap();
        assertThat(requirements)
            .as("Requirements list")
            .isNotNull();

        assertThat(requirements)
            .as("Requirement types")
            .extracting(RequirementDetailsBean::getType).contains(EPIC);

        assertThat(requirements)
            .as("Requirement IDs")
            .extracting(RequirementDetailsBean::getId).contains(EPIC_ID_1, EPIC_ID_2);
    }

    @Test
    public void getEpicRequirements() {
        String query = null;
        DocumentList<RequirementDetailsBean> response = controller.getRequirements(EPIC_ID_1, PAGE, SIZE, ORDER_BY, ASC,
            query);
        assertPageResponse(response, 2L);

        List<RequirementDetailsBean> requirements = response.unwrap();
        assertThat(requirements)
            .as("Requirements list")
            .isNotNull();

        assertThat(requirements)
            .as("Requirement types")
            .extracting(RequirementDetailsBean::getType).contains(STORY);

        assertThat(requirements)
            .as("Requirement IDs")
            .extracting(RequirementDetailsBean::getId).contains(STORY_ID_1, STORY_ID_2);
    }

    @Test
    public void getUserStoryRequirements() {
        String query = null;
        DocumentList<RequirementDetailsBean> response = controller.getRequirements(STORY_ID_1, PAGE, SIZE, ORDER_BY,
            ASC, query);
        assertPageResponse(response, 0L);

        List<RequirementDetailsBean> requirements = response.unwrap();
        assertThat(requirements)
            .as("Requirements list")
            .isNullOrEmpty();
    }

    @Test
    public void getSingleEpicRequirement() {
        Document<RequirementDetailsBean> result = controller.getRequirement(EPIC_ID_1);

        assertThat(result)
            .as("Requirement response")
            .isNotNull();

        assertThat(result.getData()).isNotNull();
        assertThat(result.getErrors()).isNull();
        RequirementDetailsBean requirement = result.unwrap();

        assertThat(requirement)
            .as("Requirement")
            .isNotNull();

        assertThat(requirement.getId())
            .as("Requirement ID")
            .isEqualTo(EPIC_ID_1);

        assertThat(requirement.getType())
            .as("Requirement Type")
            .isEqualTo(EPIC);
    }

    @Test
    public void getNonExisting() {
        assertThatThrownBy(() -> controller.getRequirement("non-existing"))
            .as("Retrieve non-existing requirement")
            .isInstanceOf(NotFoundException.class);
    }

    private static ProjectRequirementBean createRequirement(String id, String name, RequirementType type) {
        ProjectRequirementBean requirement = new ProjectRequirementBean();
        requirement.setId(id);
        requirement.setName(name);
        requirement.setType(type);
        requirement.setContextId(CONTEXT_ID);
        return requirement;
    }

    private static void assertPageResponse(DocumentList<RequirementDetailsBean> response, long totalItems) {
        assertThat(response)
            .as("Response of requirements results")
            .isNotNull();
        assertThat(response.getErrors())
            .as("Errors of requirements response")
            .isNullOrEmpty();
        assertThat(response.getMeta())
            .as("Meta of requirement response")
            .isNotNull();
        assertThat(response.getMeta().getMeta())
            .as("Map of meta response")
            .isNotEmpty()
            .contains(
                entry(TOTAL_ITEMS, totalItems),
                entry(ITEM_PER_PAGE, SIZE),
                entry(CURRENT_PAGE, PAGE));
    }

    private void assertRequirement(String requirementId, ProjectRequirementBean expectedRequirement) {
        Document<RequirementDetailsBean> projectResponse = controller.getRequirement(requirementId);

        assertThat(projectResponse)
            .as("Requirement response")
            .isNotNull();

        RequirementDetailsBean requirement = projectResponse.unwrap();
        assertThat(requirement)
            .as("Requirement bean")
            .isNotNull();

        assertThat(requirement.getId())
            .as("Requirement ID")
            .isEqualTo(expectedRequirement.getId());

        assertThat(requirement.getName())
            .as("Requirement name")
            .isEqualTo(expectedRequirement.getName());

        assertThat(requirement.getType())
            .as("Requirement type")
            .isEqualTo(expectedRequirement.getType());
    }
}

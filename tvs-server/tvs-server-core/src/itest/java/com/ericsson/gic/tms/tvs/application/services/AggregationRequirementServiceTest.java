package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirement;
import com.ericsson.gic.tms.tvs.domain.repositories.ProjectRequirementRepository;
import com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ericsson.gic.tms.tvs.application.services.TvsBeanFactory.*;
import static com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirementType.*;
import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

public class AggregationRequirementServiceTest extends AbstractIntegrationTest {

    @Autowired
    private AggregationRequirementService aggregationRequirementService;

    @Autowired
    private TestCaseResultService resultService;

    @Autowired
    private ProjectRequirementRepository repository;

    @Before
    public void setUp() throws Exception {
        String userStoryId = "REQ-SERVICE-TEST-US";
        String userStoryId2 = "REQ-SERVICE-TEST-US2";
        String epicId = "REQ-SERVICE-TEST-EPIC";
        String mainReqId = "REQ-SERVICE-TEST-MR";

        ProjectRequirement us = new ProjectRequirement();
        us.setId(userStoryId);
        us.setType(STORY);

        ProjectRequirement us2 = new ProjectRequirement();
        us2.setId(userStoryId2);
        us2.setType(STORY);

        ProjectRequirement epic = new ProjectRequirement();
        epic.setId(epicId);
        epic.addChildren(us, us2);
        epic.setType(EPIC);

        ProjectRequirement mr = new ProjectRequirement();
        mr.setId(mainReqId);
        mr.addChildren(epic);
        mr.setType(MR);

        repository.save(newArrayList(us, us2, epic, mr));

        TestCaseResultBean tCR = testCaseResult("1", "PASSED");
        tCR.addAdditionalFields(FieldNameConst.REQUIREMENTS, singleton(userStoryId));
        TestCaseResultBean tCR2 = testCaseResult("2", "FAILED");
        tCR2.addAdditionalFields(FieldNameConst.REQUIREMENTS, singleton(userStoryId));
        TestCaseResultBean tCR3 = testCaseResult("3", "BROKEN");
        tCR3.addAdditionalFields(FieldNameConst.REQUIREMENTS, singleton(userStoryId));
        TestCaseResultBean tCR4 = testCaseResult("4", "PASSED");
        tCR4.addAdditionalFields(FieldNameConst.REQUIREMENTS, newArrayList(userStoryId2, userStoryId));

        ResultPath resultPath = new ResultPath()
            .withJob("systemId-1", "null")
            .withTestSession("null", "null")
            .withTestSuiteResult("null", "null");
        resultService.updateTestCaseResults(resultPath, newArrayList(tCR, tCR2, tCR3, tCR4));
    }

    @Test
    public void testAggregateUserStories() throws Exception {
        aggregationRequirementService.aggregateUserStories();

        ProjectRequirement userStory = repository.findOne("REQ-SERVICE-TEST-US");

        assertThat(userStory)
            .isNotNull();

        assertThat(userStory.getAdditionalFields())
            .hasSize(4)
            .contains(
                entry("testCaseCount", 4),
                entry("passedTestCaseCount", 2),
                entry("failedTestCaseCount", 1),
                entry("passRate", 50.0)
            );
    }

    @Test
    public void testAggregateEpics() throws Exception {
        aggregationRequirementService.aggregateUserStories();
        aggregationRequirementService.aggregateEpics();

        ProjectRequirement epic = repository.findOne("REQ-SERVICE-TEST-EPIC");

        assertThat(epic)
            .isNotNull();

        assertThat(epic.getAdditionalFields())
            .hasSize(5)
            .contains(
                entry("testCaseCount", 5),
                entry("userStoryCount", 2),
                entry("usWithTestResults", 2),
                entry("SOC", 100.0),
                entry("SOV", 50.0)
            );
    }

    @Test
    public void testAggregateMainRequirements() throws Exception {
        aggregationRequirementService.aggregateUserStories();
        aggregationRequirementService.aggregateEpics();
        aggregationRequirementService.aggregateMainRequirements();

        ProjectRequirement userStory = repository.findOne("REQ-SERVICE-TEST-MR");

        assertThat(userStory)
            .isNotNull();

        assertThat(userStory.getAdditionalFields())
            .hasSize(6)
            .contains(
                entry("epicCount", 1),
                entry("testCaseCount", 5),
                entry("userStoryCount", 2),
                entry("usWithTestResults", 2),
                entry("SOC", 100.0),
                entry("SOV", 0.0)
            );
    }
}

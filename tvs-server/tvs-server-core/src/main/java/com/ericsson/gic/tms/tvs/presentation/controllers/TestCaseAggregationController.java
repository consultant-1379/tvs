package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.tvs.application.services.TestCaseAggregationService;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseIdBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestVerdictBean;
import com.ericsson.gic.tms.tvs.presentation.resources.TestVerdictResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class TestCaseAggregationController
    extends AbstractJsonApiCapableController
    implements TestVerdictResource {

    @Autowired
    private TestCaseAggregationService service;

    @Override
    public DocumentList<TestCaseIdBean> getTestResultIds(LocalDateTime dateFrom) {
        return responseFor(service.getTestCaseResultIds(dateFrom))
            .withSelfRel(TestVerdictResource.class)
            .build();
    }

    @Override
    public Document<TestVerdictBean> getTestResult(String testCaseId) {
        return responseFor(service.getTestCaseResultStatistics(testCaseId))
            .withSelfRel(TestVerdictResource.class, TEST_CASE_ID)
            .build();
    }
}

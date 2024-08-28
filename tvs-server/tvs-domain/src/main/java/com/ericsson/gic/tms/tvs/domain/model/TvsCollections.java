package com.ericsson.gic.tms.tvs.domain.model;

public enum TvsCollections {
    JOB("job"),
    TEST_SESSION("testSession"),
    TEST_SUITE_RESULT("testSuiteResult"),
    TEST_CASE_RESULT("testCaseResult"),
    RESULT_CODE("resultCode"),
    COLLECTION_METADATA("collectionMetadata"),
    PROJECT_REQUIREMENT("projectRequirement"),
    JOB_CONFIGURATION("jobConfiguration"),
    JOB_NOTIFICATION("jobNotification"),
    USER_SESSION("userSession"),
    URL_USAGE("urlUsage"),
    ALLURE_REPORT_LOG("allureReportLog");

    private String name;

    TvsCollections(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

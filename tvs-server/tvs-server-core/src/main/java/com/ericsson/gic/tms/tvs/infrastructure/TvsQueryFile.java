package com.ericsson.gic.tms.tvs.infrastructure;

import com.ericsson.gic.tms.infrastructure.MongoQueryFile;

public enum TvsQueryFile implements MongoQueryFile {

    TEST_CASE_RESULT_STATISTICS("classpath:mongo/test-case-result-statistics.json"),
    TEST_CASE_RESULT_IDS("classpath:mongo/test-case-result-ids.json"),
    JOB_STATISTICS_AGGREGATION("classpath:mongo/job-statistics-aggregation.json"),
    JOB_STATISTICS_MATCHING("classpath:mongo/job-statistics-matching.json"),
    PROJECT_TEST_CASE_ID("classpath:mongo/project-test-case-id.json"),
    MATCH_NAME("classpath:mongo/match-name.json"),
    MATCH_GT_DATE("classpath:mongo/match-gt-date.json"),

    FLAKINESS_FILTER_BY_JOB_AND_TIME("classpath:mongo/flakiness/filter-by-job-and-time.json"),
    FLAKINESS_FILTER_BY_JOB_AND_TESTCASE_AND_DATE("classpath:mongo/flakiness/filter-by-job-and-testcase-and-date.json"),
    FLAKINESS_GROUP_BY_TEST_CASE("classpath:mongo/flakiness/group-by-test-case.json"),

    TEST_SUITE_RESULT_STATISTICS("classpath:mongo/test-suite/test-suite-result-statistics.json"),

    TEST_SESSION_FIELDS_SUITE_MATCH("classpath:mongo/test-session/fields/test-suite-match.json"),
    TEST_SESSION_FIELDS_SUITE_SORT("classpath:mongo/test-session/fields/test-suite-sort.json"),
    TEST_SESSION_FIELDS_SUITE_GROUP("classpath:mongo/test-session/fields/test-suite-group.json"),
    TEST_SESSION_FIELDS_SUITE_PROJECT("classpath:mongo/test-session/fields/test-suite-project.json"),

    JOB_FIELDS_SESSION_MATCH("classpath:mongo/job/fields/test-session-match.json"),
    JOB_FIELDS_SESSION_SORT("classpath:mongo/job/fields/test-session-sort.json"),
    JOB_FIELDS_SESSION_GROUP("classpath:mongo/job/fields/test-session-group.json"),

    DROP_REPORT_AGGREGATION_MATCH("classpath:mongo/job/report/drop/last-iso/drop-report-match.json"),
    DROP_REPORT_AGGREGATION_SORT("classpath:mongo/job/report/drop/last-iso/drop-report-sort.json"),
    DROP_REPORT_AGGREGATION_GROUP("classpath:mongo/job/report/drop/last-iso/drop-report-group.json"),
    DROP_REPORT_AGGREGATION_FINAL_SORT("classpath:mongo/job/report/drop/last-iso/drop-report-final-sort.json"),

    ISO_REPORT_MATCH("classpath:mongo/test-case/reports/iso/match.json"),
    ISO_REPORT_UNWIND_GROUPS("classpath:mongo/test-case/reports/iso/unwind_groups.json"),
    ISO_REPORT_UNWIND_COMPONENTS("classpath:mongo/test-case/reports/iso/unwind_components.json"),
    ISO_REPORT_PROJECT("classpath:mongo/test-case/reports/iso/project.json"),
    ISO_REPORT_GROUP_GROUP("classpath:mongo/test-case/reports/iso/group_group.json"),
    ISO_REPORT_SORT_GROUP("classpath:mongo/test-case/reports/iso/sort_group.json"),
    ISO_REPORT_GROUP_PRIORITY("classpath:mongo/test-case/reports/iso/group_priority.json"),
    ISO_REPORT_SORT_PRIORITY("classpath:mongo/test-case/reports/iso/sort_priority.json"),
    ISO_REPORT_GROUP_COMPONENT("classpath:mongo/test-case/reports/iso/group_component.json"),
    ISO_REPORT_SORT_COMPONENT("classpath:mongo/test-case/reports/iso/sort_component.json"),
    ISO_REPORT_PROJECT_PASS_RATE("classpath:mongo/test-case/reports/iso/project_pass_rate.json"),

    TC_RESULTS_TREND_MATCH("classpath:mongo/test-case/reports/trend/match.json"),
    TC_RESULTS_TREND_PROJECT("classpath:mongo/test-case/reports/trend/project.json"),
    TC_RESULTS_TREND_PROJECT_PASS_RATE("classpath:mongo/test-case/reports/trend/project_pass_rate.json"),
    TC_RESULTS_TREND_GROUP_BY_ISO_AND_TAG("classpath:mongo/test-case/reports/trend/group_by_iso_and_tag.json"),
    TC_RESULTS_TREND_GROUP_BY_ISO("classpath:mongo/test-case/reports/trend/group_by_iso.json"),
    TC_RESULTS_TREND_UNWIND_TAG("classpath:mongo/test-case/reports/trend/unwind_tag.json"),

    TC_RESULTS_TREND_RC_GROUP_BY_ISO_AND_TAG(
        "classpath:mongo/test-case/reports/trend/result-codes/group_by_iso_and_tag.json"),
    TC_RESULTS_TREND_RC_GROUP_BY_TAG("classpath:mongo/test-case/reports/trend/result-codes/group_by_tag.json"),
    TC_RESULTS_TREND_RC_MATCH("classpath:mongo/test-case/reports/trend/result-codes/match.json"),
    TC_RESULTS_TREND_RC_PROJECT_IN_GROUP("classpath:mongo/test-case/reports/trend/result-codes/project_in_group.json"),
    TC_RESULTS_TREND_RC_PROJECT_IN_TAG("classpath:mongo/test-case/reports/trend/result-codes/project_in_tag.json"),
    TC_RESULTS_TREND_RC_UNWIND_DATA("classpath:mongo/test-case/reports/trend/result-codes/unwind_data.json"),

    REQ_MATCH("classpath:mongo/requirement/match-requirement.json"),
    REQ_PROJECT("classpath:mongo/requirement/project-req.json"),

    REQ_US_UNWIND("classpath:mongo/requirement/user-stories/unwind.json"),
    REQ_US_SORT("classpath:mongo/requirement/user-stories/sort.json"),
    REQ_US_MATCH("classpath:mongo/requirement/user-stories/match.json"),
    REQ_US_GROUP1("classpath:mongo/requirement/user-stories/group1.json"),
    REQ_US_GROUP2("classpath:mongo/requirement/user-stories/group2.json"),
    REQ_US_PROJECT("classpath:mongo/requirement/user-stories/project.json"),

    REQ_EPICS_MATCH("classpath:mongo/requirement/epics/match.json"),
    REQ_EPICS_GROUP("classpath:mongo/requirement/epics/group.json"),
    REQ_EPICS_PROJECT("classpath:mongo/requirement/epics/project.json"),

    REQ_MR_MATCH("classpath:mongo/requirement/main-requirements/match.json"),
    REQ_MR_GROUP("classpath:mongo/requirement/main-requirements/group.json"),
    REQ_MR_PROJECT("classpath:mongo/requirement/main-requirements/project.json"),

    DROP_REPORT_CUMULATIVE_MATCH("classpath:mongo/job/report/drop/cumulative/match.json"),
    DROP_REPORT_CUMULATIVE_MATCH_BY_RESULT("classpath:mongo/job/report/drop/cumulative/match-by-result.json"),
    DROP_REPORT_CUMULATIVE_INITIAL_SORT("classpath:mongo/job/report/drop/cumulative/initial-sort.json"),
    DROP_REPORT_CUMULATIVE_GROUP_RESULT_SET("classpath:mongo/job/report/drop/cumulative/group-result-set.json"),
    DROP_REPORT_CUMULATIVE_UNWIND("classpath:mongo/job/report/drop/cumulative/unwind.json"),
    DROP_REPORT_CUMULATIVE_GROUP_LAST_RESULT("classpath:mongo/job/report/drop/cumulative/group-last-result.json"),
    DROP_REPORT_CUMULATIVE_GROUP_PRE_PROJECT("classpath:mongo/job/report/drop/cumulative/group-pre-project.json"),
    DROP_REPORT_CUMULATIVE_PROJECT("classpath:mongo/job/report/drop/cumulative/project.json"),
    DROP_REPORT_CUMULATIVE_FINAL_SORT("classpath:mongo/job/report/drop/cumulative/final-sort.json");

    private final String path;

    TvsQueryFile(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}

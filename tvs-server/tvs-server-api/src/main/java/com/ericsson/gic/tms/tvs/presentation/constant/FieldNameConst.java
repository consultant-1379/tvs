package com.ericsson.gic.tms.tvs.presentation.constant;

import java.util.Arrays;
import java.util.List;

public final class FieldNameConst {

    public static final String COMPONENTS = "COMPONENTS";
    public static final String DROP_NAME = "DROP_NAME";
    public static final String EPIC_COUNT = "epicCount";
    public static final String GROUPS = "GROUPS";
    public static final String ISO_VERSION = "ISO_VERSION";
    public static final String ISO_VERSION_PADDED = "ISO_VERSION_PADDED";
    public static final String ISO_ARTIFACT_ID = "ISO_ARTIFACT_ID";
    public static final String JENKINS_JOB_NAME = "JENKINS_JOB_NAME";
    public static final String JOB_ID = "jobId";
    public static final String PRIORITY = "PRIORITY";
    public static final String REQUIREMENTS = "REQUIREMENTS";
    public static final String RESULT_CODE = "resultCode";
    public static final String SOV = "SOV";
    public static final String SOC = "SOC";
    public static final String SUITE_NAME = "testSuiteName";
    public static final String TITLE = "TITLE";
    public static final String TEST_CASE_COUNT = "testCaseCount";
    public static final String USER_STORY_COUNT = "userStoryCount";
    public static final String US_WITH_TEST_RESULT_COUNT = "usWithTestResults";

    private static final List<String> ARRAY_FIELDS = Arrays.asList(COMPONENTS, GROUPS, REQUIREMENTS);

    private FieldNameConst() {
        // no constructor expect
    }

    public static boolean isArrayField(String fieldName) {
        return ARRAY_FIELDS.contains(fieldName);
    }
}

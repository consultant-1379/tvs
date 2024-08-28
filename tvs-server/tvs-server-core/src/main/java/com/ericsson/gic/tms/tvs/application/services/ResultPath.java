package com.ericsson.gic.tms.tvs.application.services;

import com.google.common.base.Preconditions;

public final class ResultPath {

    private final String contextId;
    private final String jobId;
    private final String executionId;
    private final String testSessionId;
    private final String testSuiteName;
    private final String testSuiteResultId;

    private ResultPath(String contextId,
                      String jobId,
                      String executionId,
                      String testSessionId,
                      String testSuiteName,
                      String testSuiteResultId) {
        this.contextId = contextId;
        this.jobId = jobId;
        this.executionId = executionId;
        this.testSessionId = testSessionId;
        this.testSuiteName = testSuiteName;
        this.testSuiteResultId = testSuiteResultId;
    }

    public ResultPath() {
        this(null, null, null, null, null, null);
    }

    public String getContextId() {
        return contextId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getTestSessionId() {
        return testSessionId;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public String getTestSuiteResultId() {
        return testSuiteResultId;
    }

    public ResultPath withJob(String contextId, String jobId) {
        return new ResultPath(contextId, jobId, executionId, testSessionId, testSuiteName, testSuiteResultId);
    }

    public ResultPath withTestSession(String executionId, String testSessionId) {
        Preconditions.checkNotNull(contextId);
        Preconditions.checkNotNull(jobId);
        return new ResultPath(contextId, jobId, executionId, testSessionId, testSuiteName, testSuiteResultId);
    }

    public ResultPath withTestSuiteResult(String testSuiteName, String testSuiteResultId) {
        Preconditions.checkNotNull(contextId);
        Preconditions.checkNotNull(jobId);
        Preconditions.checkNotNull(executionId);
        Preconditions.checkNotNull(testSessionId);
        return new ResultPath(contextId, jobId, executionId, testSessionId, testSuiteName, testSuiteResultId);
    }
}

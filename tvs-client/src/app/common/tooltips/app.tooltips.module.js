const moduleName = 'app.tooltips';

angular.module(moduleName, [])
    .constant('jobsTooltips', {
        name: 'Test Activity Name',
        context: 'The context corresponding to this Test Session',
        testSessionCount: 'The number of Test Sessions in each Test Activity',
        lastTestSessionTestSuiteCount: 'The number of suites contained in the last Test Session that was executed',
        lastTestSessionTestCaseCount: 'The number of Test Cases contained in the last Test Session that was executed',
        lastExecutionDate: 'The last test execution date',
        lastTestSessionDuration: 'The total execution time of the last Test Session that was executed',
        avgTestSessionDuration: 'The average execution time for all Test Session executions'
    })
    .constant('jobTooltips', {
        executionId: 'The execution Id corresponds to the Id from the Allure report',
        startDate: 'The start date and time for the Test Session execution',
        stopDate: 'The end date and time for the Test Session execution',
        duration: 'The total time taken for this Test Session to execute',
        testCaseCount: 'The number of Tests executed in the Test Session',
        testSuiteCount: 'The number of Test Suites executed in the Test Session',
        passRate: 'The percentage of Tests that passed in the executed Test Session',
        isoVersion: 'The ISO version for the Test Session',
        dropName: 'The drop that the Test Session was executed against',
        uri: 'Link to the Jenkins Job executed',
        resultCode: 'The Pass/Fail status of the executed Job',
        lastExecutionDate: 'The date and time of the last execution of this Test Session',
        createdDate: 'The date and time that this Test Session was created',
        jenkinsJobName: 'The name of the Jenkins Job that corresponds to this Test Session',
        isoArtifactId: 'The Id of the ISO that this Test Session was executed against',
        modifiedDate: 'The last date and time that this job was modified on'
    })
    .constant('testSuiteTooltips', {
        name: 'Test Suite ID',
        duration: 'The total time taken to execute the Test Suite',
        startDate: 'The start Date and time for the Test Suite execution',
        stopDate: 'The end Date and time for the Test Suite execution',
        passRate: 'The percentage of executed tests in the suite that passed',
        createdDate: 'The date that the Test Suite was created on',
        total: 'The total number of Test Cases in the suite',
        passed: 'The number of passed Test Cases in the suite',
        pending: 'The number of Test Cases with results still pending in the suite',
        cancelled: 'The number of Test Cases in the suite that were cancelled during execution',
        failed: 'The number of failing Test Cases in the suite',
        broken: 'The number of broken Test Cases in the suite'
    })
    .constant('testCaseTooltips', {
        name: 'Test Case ID',
        title: 'Test Case Title',
        duration: 'The total time taken to execute the Test Case',
        startDate: 'The start date and time for the Test Case execution',
        stopDate: 'The end Date and time for the Test Case execution',
        resultCode: 'The Pass/Fail/Pending status of the executed Test Case',
        requirements: 'The user stories that are linked to this Test Case, taken from TMS',
        groups: 'The groups that this Test Case belongs to, taken from TMS',
        priority: 'The priority of this Test Case, taken from TMS',
        component: 'The component for this Test Case, taken from TMS',
        createdDate: 'The date and time that this Test Case was created',
        modifiedDate: 'The last date and time that this Test Case was modified on',
        isoVersion: 'The ISO version that this Test Case was executed against',
        dropName: 'The drop that the Test Case was executed against'
    })
    .constant('trendReportTooltips', {
        passed: 'Number of Passed Test Cases',
        failed: 'Number of Failed Test Cases',
        isoVersion: 'ISO Version'

    })
    .constant('isoReportTooltips', {
        priority: 'Test Case Priorities, taken from TMS',
        group: 'Test Case Group, taken from TMS',
        component: 'Test Case Component, taken from TMS',
        total: 'The total number of Test Cases for the selected ISO and Test Activities',
        passed: 'The total number of passing Test Cases for the selected ISO and Test Activities',
        failed: 'The total number of failing Test Cases for the selected ISO and Test Activities',
        broken: 'The total number of broken Test Cases for the selected ISO and Test Activities',
        cancelled: 'The total number of cancelled Test Cases for the selected ISO and Test Activities',
        passRate: 'The percentage of passing Test Cases for the selected ISO and Test Activities'
    })
    .constant('requirementsTooltips', {
        requirementId: 'Main Requirement ID from JIRA',
        name: 'Main Requirement Name',
        epicCount: 'Number of Epics in the MR',
        testCaseCount: 'Number of Test Cases in the MR',
        userStoryCount: 'Number of User Stories in the MR',
        usWithTestResults: 'Number of User Stories in the MR with Test Results',
        SOC: 'Statement of Compliance',
        SOV: 'Statement of Verification',
        linkToIssue: 'Link to MR in JIRA'
    })
    .constant('epicsTooltips', {
        epicId: 'Epic ID from JIRA',
        name: 'Epic Name',
        testCaseCount: 'Number of Test Cases in the Epic',
        userStoryCount: 'Number of User Stories in the Epic',
        usWithTestResults: 'Number of User Stories in the Epic with Test Results',
        SOC: 'Statement of Compliance',
        SOV: 'Statement of Verification',
        linkToIssue: 'Link to Epic in JIRA'
    })
    .constant('userStoryTooltips', {
        userStoryId: 'User Story ID from JIRA',
        name: 'User Story Name',
        testCaseCount: 'Number of Test Cases for the User Story',
        passedTestCaseCount: 'Number of passed Test Cases for the User Story',
        failedTestCaseCount: 'Number of failed Test Cases for the User Story',
        passRate: 'Percentage of passing Test Cases for the User Story',
        linkToIssue: 'Link to the User Story in JIRA'
    })
    .constant('dropReportTooltips', {
        dropName: 'Drop Name',
        latestIsoVersion: 'Last ISO version in this Drop',
        passRate: 'Percentage of Passing Test Cases for the Drop and ISO Version',
        testCasesCount: 'Number of Test Cases for the Drop and ISO Version',
        passedTestCaseCount: 'Number of passing Test Cases for the Drop and ISO Version',
        failedTestCaseCount: 'Number of failing Test Cases for the Drop and ISO Version',
        testSuitesCount: 'Number of Test Suites for the Drop and ISO Version',
        testSessionsCount: 'Number of Test Sessions for the Drop and ISO Version',
        isoLastStartTime: 'Last Test Session Execution Time for this Drop and ISO Version'
    });

export default moduleName;

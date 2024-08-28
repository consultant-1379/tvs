import contextResource from './context-resource';
import contextUserRoleResource from './context-user-role-resource';
import jobsResource from './jobs-resource';
import notificationRulesResource from './notification-rules-resource';
import notificationRecipientsResource from './notification-recipients-resource';
import jobsInsertResource from './jobs-insert-resource';
import testSessionsResource from './test-sessions-resource';
import testSessionsInsertResource from './test-sessions-insert-resource';
import testSuiteResultsResource from './test-suite-results-resource';
import testCaseReportsResource from './test-case-reports-resource';
import testCaseResultsResource from './test-case-results-resource';
import testCaseExecutionHistoryResource from './test-case-execution-history-resource';
import referencesResource from './references-resource';
import reportsResource from './drop-reports-resource';
import contextReportsResource from './context-reports-resource';
import resultCodeResource from './result-code-resource';
import requirementsResource from './requirements-resource';
import userResource from './user-resource';
import dashboardResource from './dashboard-resource';

const moduleName = 'app.resources';

angular.module(moduleName, [])
    .factory('contextResource', contextResource)
    .factory('contextReportsResource', contextReportsResource)
    .factory('contextUserRoleResource', contextUserRoleResource)
    .factory('jobsResource', jobsResource)
    .factory('jobsInsertResource', jobsInsertResource)
    .factory('notificationRulesResource', notificationRulesResource)
    .factory('notificationRecipientsResource', notificationRecipientsResource)
    .factory('testSessionsResource', testSessionsResource)
    .factory('testSessionsInsertResource', testSessionsInsertResource)
    .factory('testSuiteResultsResource', testSuiteResultsResource)
    .factory('testCaseReportsResource', testCaseReportsResource)
    .factory('testCaseResultsResource', testCaseResultsResource)
    .factory('testCaseExecutionHistoryResource', testCaseExecutionHistoryResource)
    .factory('referencesResource', referencesResource)
    .factory('reportsResource', reportsResource)
    .factory('resultCodeResource', resultCodeResource)
    .factory('requirementsResource', requirementsResource)
    .factory('dashboardResource', dashboardResource)
    .factory('userResource', userResource);

export default moduleName;

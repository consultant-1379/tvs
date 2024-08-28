import JobsViewController from './jobs-view/jobs-view-controller';
import JobViewController from './job-view/job-view-controller';
import TestFlakinessViewController from './test-flakiness-view/test-flakiness-view-controller';
import TestExecutionFlakinessViewController
    from './test-execution-flakiness-view/test-execution-flakiness-view-controller';
import TestSessionViewController from './test-session-view/test-session-view-controller';
import TestSuiteResultViewController from './test-suite-result-view/test-suite-result-view-controller';
import TestCaseExecutionHistoryViewController
    from './test-case-execution-history-view/test-case-execution-history-view-controller';
import jobsViewDirective from './jobs-view/jobs-view-directive';
import jobViewDirective from './job-view/job-view-directive';
import testFlakinessViewDirective from './test-flakiness-view/test-flakiness-view-directive';
import testExecutionFlakinessViewDirective
    from './test-execution-flakiness-view/test-execution-flakiness-view-directive';
import testSessionViewDirective from './test-session-view/test-session-view-directive';
import testSuiteResultViewDirective from './test-suite-result-view/test-suite-result-view-directive';
import testCaseExecutionHistoryViewDirective
    from './test-case-execution-history-view/test-case-execution-history-view-directive';

const moduleName = 'app.test-results';

angular.module(moduleName, [])
    .directive('jobsView', jobsViewDirective)
    .directive('jobView', jobViewDirective)
    .directive('testFlakinessView', testFlakinessViewDirective)
    .directive('testExecutionFlakinessView', testExecutionFlakinessViewDirective)
    .directive('testSessionView', testSessionViewDirective)
    .directive('testSuiteResultView', testSuiteResultViewDirective)
    .directive('testCaseExecutionHistoryView', testCaseExecutionHistoryViewDirective)

    .controller('JobsViewController', JobsViewController)
    .controller('JobViewController', JobViewController)
    .controller('TestFlakinessViewController', TestFlakinessViewController)
    .controller('TestExecutionFlakinessViewController', TestExecutionFlakinessViewController)
    .controller('TestSessionViewController', TestSessionViewController)
    .controller('TestSuiteResultViewController', TestSuiteResultViewController)
    .controller('TestCaseExecutionHistoryViewController', TestCaseExecutionHistoryViewController);

export default moduleName;

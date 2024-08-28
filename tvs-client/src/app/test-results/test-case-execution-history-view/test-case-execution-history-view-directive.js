export default function testCaseExecutionHistoryViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/test-results/test-case-execution-history-view/test-case-execution-history-view.html',
        scope: {
            testCaseId: '='
        },
        bindToController: true,
        controller: 'TestCaseExecutionHistoryViewController',
        controllerAs: 'vm'
    };
}

export default function testExecutionFlakinessViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/test-results/test-execution-flakiness-view/test-execution-flakiness-view.html',
        scope: {},
        bindToController: true,
        controller: 'TestExecutionFlakinessViewController',
        controllerAs: 'vm'
    };
}

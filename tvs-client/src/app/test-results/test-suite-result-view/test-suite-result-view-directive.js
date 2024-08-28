export default function testSuiteViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/test-results/test-suite-result-view/test-suite-result-view.html',
        scope: {
            testSuiteResult: '='
        },
        bindToController: true,
        controller: 'TestSuiteResultViewController',
        controllerAs: 'vm'
    };
}

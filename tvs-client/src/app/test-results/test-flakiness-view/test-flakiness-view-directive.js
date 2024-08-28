export default function testFlakinessViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/test-results/test-flakiness-view/test-flakiness-view.html',
        scope: {
            job: '='
        },
        bindToController: true,
        controller: 'TestFlakinessViewController',
        controllerAs: 'vm'
    };
}

export default function testSessionViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/test-results/test-session-view/test-session-view.html',
        scope: {
            testSession: '='
        },
        bindToController: true,
        controller: 'TestSessionViewController',
        controllerAs: 'vm'
    };
}

export default function testCaseStatusDirective() {
    return {
        restrict: 'E',
        scope: {
            statusValue: '@'
        },
        templateUrl: 'app/common/test-case-status/test-case-status.html'
    };
}

import testCaseStatusDirective from './test-case-status-directive';

const moduleName = 'app.tvs-test-case-status';

angular.module(moduleName, [])
    .directive('testCaseStatus', testCaseStatusDirective);

export default moduleName;

export default function jobViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/test-results/job-view/job-view.html',
        scope: {
            job: '='
        },
        bindToController: true,
        controller: 'JobViewController',
        controllerAs: 'vm'
    };
}

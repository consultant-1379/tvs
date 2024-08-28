export default function jobsViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/test-results/jobs-view/jobs-view.html',
        scope: {
            jobs: '=?'
        },
        bindToController: true,
        controller: 'JobsViewController',
        controllerAs: 'vm'
    };
}

export default function() {
    return {
        restrict: 'E',
        templateUrl: 'app/administration/job-administration/job-administration.html',
        scope: {},
        bindToController: true,
        controller: 'JobAdministrationController',
        controllerAs: 'vm'
    };
}

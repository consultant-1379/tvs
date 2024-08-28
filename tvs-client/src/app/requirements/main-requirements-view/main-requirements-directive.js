export default function mainRequirementsViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/requirements/main-requirements-view/main-requirements-view.html',
        scope: {},
        bindToController: true,
        controller: 'MainRequirementsViewController',
        controllerAs: 'vm'
    };
}

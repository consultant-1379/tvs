export default function mainRequirementsViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/requirements/user-stories-view/user-stories-view.html',
        scope: {},
        bindToController: true,
        controller: 'UserStoriesViewController',
        controllerAs: 'vm'
    };
}

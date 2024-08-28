export default function epicsViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/requirements/epics-view/epics-view.html',
        scope: {},
        bindToController: true,
        controller: 'EpicsViewController',
        controllerAs: 'vm'
    };
}

export default function tvsNavbarDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/common/navbar/tvs-navbar.html',
        scope: {},
        bindToController: true,
        controller: 'TvsNavbarController',
        controllerAs: 'vm'
    };
}

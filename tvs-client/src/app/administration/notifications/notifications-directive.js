export default function notificationsDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/administration/notifications/notifications.html',
        scope: {},
        bindToController: true,
        controller: 'NotificationsController',
        controllerAs: 'vm'
    };
}

export default function loginFormDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/login/login-form/login-form.html',
        scope: {},
        bindToController: true,
        controller: 'LoginFormController',
        controllerAs: 'vm'
    };
}

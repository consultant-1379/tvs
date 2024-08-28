export default class LoginController {

    constructor($scope, $state, $stateParams, $window, userSessionService, userRoles) {
        'ngInject';

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.$window = $window;
        this.userSessionService = userSessionService;

        this.redirects = [
            {roles: [userRoles.contextAdmin, userRoles.securityAdmin], state: 'contexts'},
            {roles: [userRoles.testManager, userRoles.testEngineer], state: 'abstractTestCases.list'}
        ];

        userSessionService.watch($scope, session => {
            if (session.authenticated) {
                this.returnToOrigin();
            }
        });
    }

    returnToOrigin() {
        let to = decodeURIComponent(this.$stateParams.to);
        if (this.$stateParams && _.startsWith(to, '#/')) {
            this.$window.location.href = to;
        } else {
            this.$state.go('jobs');
        }
    }

}

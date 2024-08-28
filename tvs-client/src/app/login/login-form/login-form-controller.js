export default class LoginFormController {
    constructor(userSessionService) {
        'ngInject';
        this.userSessionService = userSessionService;

        this.login = '';
        this.password = '';
        this.errors = {};
    }

    signIn(loginForm) {
        loginForm.$setPristine();
        this.signInRequest = true;
        this.userSessionService.signIn(this.login, this.password)
            .catch(res => {
                let errors = _.get(res.data, 'errors');
                this.errors = _.groupBy(errors, 'meta.source');
            })
            .finally(() => {
                this.signInRequest = false;
            });
    }
}

export default class TceRolesService {

    constructor($q, userSessionService, userResource, $rootScope, $state) {
        'ngInject';

        this.$q = $q;
        this.userSessionService = userSessionService;
        this.userResource = userResource;

        this.$rootScope = $rootScope;
        this.$scope = $rootScope.$new(true);
        this.$state = $state;

        this.roles = null;
        this.userSessionService.watch(this.$scope, ({authenticated, name: username} = {}) => {
            if (!authenticated) {
                return;
            }
            userResource.get({username}).$promise.then(({data: {roles}}) => {
                this.roles = _.uniq(_.pluck(roles, 'roleBean.name'));
            });
        });

        this.lastState = {name: 'login'};
        this.toParams = null;
    }

    listenForStateChange() {
        this.$rootScope.$on('$stateChangeStart', (event, toState, toParams) => {
            this.lastState = toState;
            this.lastParams = toParams;
        });
    }

    hasRole(roles) {
        if (_.isEmpty(roles)) {
            return true;
        }
        return _.some(roles, role => _.contains(this.roles, role));
    }

    resolve(authorities = []) {
        let df = this.$q.defer();
        this.userSessionService.getSession(({authenticated, name: username}) => {
            let to = encodeURIComponent(this.$state.href(this.lastState.name, this.lastParams));

            if (authenticated) {
                let promise = this.$q.resolve();
                if (this.roles == null) {
                    promise = this.userResource.get({username}).$promise.then(({data: {roles}}) => {
                        this.roles = _.uniq(_.pluck(roles, 'roleBean.name'));
                        return this.roles;
                    });
                }

                promise.then(() => {
                    if (this.hasRole(authorities)) {
                        df.resolve();
                    } else {
                        df.reject();
                        let auth = authorities.join(',');
                        this.redirect('forbidden', {to, auth});
                    }
                });

            } else {
                df.reject();
                if (this.lastState.name !== 'login') {
                    this.redirect('login', {to});
                }
            }
        });

        return df.promise;
    }

    redirect(stateName, params) {
        this.$state.go(stateName, params, {location: 'replace'});
    }

}

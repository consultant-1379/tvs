export default function(authResolver, statusService, tceRolesService) {
    'ngInject';

    authResolver.listenForStateChange();
    tceRolesService.listenForStateChange();
    statusService.load();
}

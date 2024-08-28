import forTceRolesDirective from './for-tce-roles-directive';
import tceRolesService from './tce-roles-service';

const moduleName = 'app.tce-roles';

angular.module(moduleName, [])
    .service('tceRolesService', tceRolesService)
    .directive('forTceRoles', forTceRolesDirective);

export default moduleName;

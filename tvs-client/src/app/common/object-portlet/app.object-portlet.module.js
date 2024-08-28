import objectPortletDirective from './object-portlet-directive';
import objectPortletController from './object-portlet-controller';

const moduleName = 'app.object-portlet';

angular.module(moduleName, [])
    .directive('objectPortlet', objectPortletDirective)

    .controller('ObjectPortletController', objectPortletController);

export default moduleName;

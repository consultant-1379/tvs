import TvsNavbarController from './tvs-navbar-controller';

import tvsNavbarDirective from './tvs-navbar-directive';

const moduleName = 'app.tvs-navbar';

angular.module(moduleName, [])
    .directive('tvsNavbar', tvsNavbarDirective)
    .controller('TvsNavbarController', TvsNavbarController);

export default moduleName;

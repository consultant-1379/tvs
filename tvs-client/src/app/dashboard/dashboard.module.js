const moduleName = 'app.dashboard';

import DashboardController from './dashboard.controller';

angular
    .module(moduleName, [])

    .controller('dashboardController', DashboardController);

export default moduleName;

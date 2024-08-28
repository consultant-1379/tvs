import uiRouterConfig from './ui-router.config';

const moduleName = 'app.config';

angular.module(moduleName, [])
    .config(uiRouterConfig);

export default moduleName;

import tvsTableService from './tvs-table-service';
import tableFilterService from './table-filter-service';

const moduleName = 'app.tvs-table';

angular.module(moduleName, [])
    .service('tvsTableService', tvsTableService)
    .service('tableFilterService', tableFilterService);

export default moduleName;

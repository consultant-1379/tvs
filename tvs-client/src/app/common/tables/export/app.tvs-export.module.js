import CsvExportController from './csv-export-controller';
import CsvExportDirective from './csv-export-directive';

const moduleName = 'app.tvs-export';

angular.module(moduleName, [])
    .directive('csvExport', CsvExportDirective)
    .controller('CsvExportController', CsvExportController);

export default moduleName;

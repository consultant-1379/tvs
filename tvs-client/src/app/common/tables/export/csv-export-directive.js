export default function csvExportDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/common/tables/export/csv-export.html',
        bindToController: true,
        controller: 'CsvExportController',
        controllerAs: 'vm',
        scope: {
            buttonText: '@',
            tip: '@',
            link: '@'
        }
    };
}

export default function dropReportViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/reports/drop-report/drop-report-view.html',
        scope: {
            job: '='
        },
        bindToController: true,
        controller: 'DropReportViewController',
        controllerAs: 'vm'
    };
}

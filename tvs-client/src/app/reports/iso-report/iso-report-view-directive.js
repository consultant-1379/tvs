export default function dropReportViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/reports/iso-report/iso-report-view.html',
        scope: {},
        bindToController: true,
        controller: 'IsoReportViewController',
        controllerAs: 'vm'
    };
}

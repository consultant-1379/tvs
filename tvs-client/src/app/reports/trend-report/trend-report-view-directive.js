export default function trendReportViewDirective() {
    return {
        restrict: 'E',
        templateUrl: 'app/reports/trend-report/trend-report-view.html',
        scope: {},
        bindToController: true,
        controller: 'TrendReportViewController',
        controllerAs: 'vm'
    };
}

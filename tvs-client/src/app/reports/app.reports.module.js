import DropReportViewController from './drop-report/drop-report-view-controller';
import IsoReportViewController from './iso-report/iso-report-view-controller';
import TrendReportViewController from './trend-report/trend-report-view-controller';

import dropReportViewDirective from './drop-report/drop-report-view-directive';
import isoReportViewDirective from './iso-report/iso-report-view-directive';
import trendReportViewDirective from './trend-report/trend-report-view-directive';
import ReportsService from './reports-service/reports-service';

const moduleName = 'app.reports';

angular.module(moduleName, [])
    .directive('dropReport', dropReportViewDirective)
    .directive('isoReport', isoReportViewDirective)
    .directive('trendReport', trendReportViewDirective)

    .controller('DropReportViewController', DropReportViewController)
    .controller('IsoReportViewController', IsoReportViewController)
    .controller('TrendReportViewController', TrendReportViewController)

    .service('reportsService', ReportsService);

export default moduleName;

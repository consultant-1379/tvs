import appShared from '../../bower_components/tms-client-shared/src/app/app.shared.module';
import appConfig from './config/app.config.module';
import appResources from './resources/app.resources.module';
import appTvsNavbar from './common/navbar/app.tvs-navbar.module';
import appTvsTestCaseStatus from './common/test-case-status/app.test-case-status.module';
import appTestResults from './test-results/app.test-results.module';
import appTvsTable from './common/tables/app.tvs-table.module';
import appTvsExport from './common/tables/export/app.tvs-export.module';
import appObjectPortlet from './common/object-portlet/app.object-portlet.module';
import appReports from './reports/app.reports.module';
import appRequirements from './requirements/app.requirements.module';
import appAdministration from './administration/app.administration.module';
import appLogin from './login/app.login.module';
import appTceRoles from './common/tce-roles/app.tce-roles.module';
import appTooltips from './common/tooltips/app.tooltips.module';
import appDashboard from './dashboard/dashboard.module';

import StatusService from './common/status-service';

import initialize from './app.initialize';

angular.module('app', [
    appShared,
    appConfig,
    appResources,
    appTvsNavbar,
    appTvsTestCaseStatus,
    appTestResults,
    appTvsTable,
    appObjectPortlet,
    appReports,
    appRequirements,
    appAdministration,
    appLogin,
    appTceRoles,
    appTooltips,
    appTvsExport,
    appDashboard,
    'ngTagsInput'
])
    .constant('apiRoot', '/api/tvs')
    .service('statusService', StatusService)
    .run(initialize);

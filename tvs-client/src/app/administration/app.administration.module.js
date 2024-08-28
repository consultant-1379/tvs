import jobAdministrationDirective from './job-administration/job-administration.directive';
import notificationsDirective from './notifications/notifications-directive';
import JobAdministrationController from './job-administration/job-administration.controller';
import NotificationsController from './notifications/notifications-controller';

const moduleName = 'app.administration';

angular.module(moduleName, [])
    .directive('jobAdministration', jobAdministrationDirective)
    .directive('notifications', notificationsDirective)

    .controller('JobAdministrationController', JobAdministrationController)
    .controller('NotificationsController', NotificationsController);

export default moduleName;

import mainRequirementsDirective from './main-requirements-view/main-requirements-directive';
import epicsDirective from './epics-view/epics-directive';
import userStoriesDirective from './user-stories-view/user-stories-directive';

import MainRequirementsController from './main-requirements-view/main-requirements-controller';
import EpicsViewController from './epics-view/epics-controller';
import UserStoriesViewController from './user-stories-view/user-stories-controller';

const moduleName = 'app.requirements';

angular.module(moduleName, [])
    .directive('mainRequirements', mainRequirementsDirective)
    .directive('epics', epicsDirective)
    .directive('userStories', userStoriesDirective)

    .controller('MainRequirementsViewController', MainRequirementsController)
    .controller('EpicsViewController', EpicsViewController)
    .controller('UserStoriesViewController', UserStoriesViewController);

export default moduleName;

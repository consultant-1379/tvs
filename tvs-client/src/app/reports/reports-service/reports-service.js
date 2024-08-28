export default class ReportsService {
    constructor() {
        'ngInject';
    }

    findJobNamesForFilter(enabledActivityLabels) {
        let allJobNames = null;
        if (enabledActivityLabels.length > 0) {
            allJobNames = enabledActivityLabels.toString();
        }
        return allJobNames;
    }

    collectTestActivityLabels(meta) {
        return _.values(_.get(meta, 'job-details'));
    }
}

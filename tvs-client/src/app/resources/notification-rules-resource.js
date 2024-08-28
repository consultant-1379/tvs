export default function notificationRulesResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot,
        '/job-notifications/:jobId/rules/:ruleId',
        {jobId: '@jobId', ruleId: '@ruleId'}
    );
}

export default function jobsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/contexts/:contextId/jobs/:jobName', {
        contextId: '@contextId',
        jobName: '@jobName'
    });
}

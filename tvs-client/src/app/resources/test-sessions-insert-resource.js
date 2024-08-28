export default function testSessionResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/contexts/:contextId/jobs/:jobName/test-sessions/:executionId', {
        contextId: '@contextId',
        jobName: '@jobName',
        executionId: '@executionId'
    });
}

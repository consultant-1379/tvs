export default function testSessionsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/jobs/:jobId/test-sessions/:id',
        {jobId: '@jobId', id: '@id'}
    );
}

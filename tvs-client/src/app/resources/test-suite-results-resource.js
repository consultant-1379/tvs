export default function testSuiteResultsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/jobs/:jobId/test-sessions/:executionId/test-suite-results/:name',
        {jobId: '@jobId', executionId: '@executionId', name: '@name'}
    );
}

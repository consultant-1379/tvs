export default function testResultsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot,
        '/jobs/:jobId/test-sessions/:executionId/test-suite-results/:testSuiteName/test-case-results/:name',
        {jobId: '@jobId', executionId: '@executionId', testSuiteName: '@testSuiteName', name: '@name'}
    );
}

export default function testCaseExecutionHistoryResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot,
        '/test-case-results/:testCaseId/history',
        {testCaseId: '@testCaseId'}
    );
}

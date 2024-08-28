export default function jobsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/jobs/:id', {id: '@id', testCaseId: '@testCaseId'}, {
        getStatistics: {
            url: '/jobs/:id/statistics',
            method: 'GET'
        },
        getFlakiness: {
            url: '/jobs/:id/test-flakiness',
            method: 'GET'
        },
        getSingleTestCaseFlakiness: {
            url: '/jobs/:id/testcase/:testCaseId/test-flakiness',
            method: 'GET'
        }
    });
}

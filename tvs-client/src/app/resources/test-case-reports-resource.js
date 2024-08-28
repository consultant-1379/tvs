export default function testCaseReportsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/contexts/:contextId/reports', {contextId: '@contextId'});
}

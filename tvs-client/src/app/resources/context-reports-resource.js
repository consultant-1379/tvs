export default function contextReportsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/contexts/:contextId/reports', {contextId: '@contextId'});
}

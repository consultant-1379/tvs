export default function dropReportsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/jobs/:jobId/reports', {jobId: '@jobId'});
}

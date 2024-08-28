export default function requirementsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot, '/project-requirements/:id', {id: '@id'}, {
        getChildren: {
            url: '/project-requirements/:id/children',
            method: 'GET'
        }
    });
}

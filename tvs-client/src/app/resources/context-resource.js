export default function contextResource(tceRoot, resourceFactory) {
    'ngInject';

    return resourceFactory.create(tceRoot, '/contexts/:id', {id: '@id'});
}

export default function userResource(resourceFactory, tceRoot) {
    'ngInject';

    return resourceFactory.create(tceRoot, '/users/:username', {
        username: '@username'
    });
}


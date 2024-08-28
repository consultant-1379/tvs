export default function contextUserRoleResource(tceRoot, resourceFactory) {
    'ngInject';

    return resourceFactory.create(tceRoot, '/contexts/:id/users/:userId/roles/:roleId', {
        id: '@id',
        userId: '@userId',
        roleId: '@roleId'
    });
}

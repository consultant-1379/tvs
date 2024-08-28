export default function($resource, tvsRoot) {
    'ngInject';

    return $resource(tvsRoot + '/metrics/users', {}, {
        get: {
            method: 'get',
            timeout: 10000,
            isArray: true
        },

        getDashboardUrls: {
            url: tvsRoot + '/metrics/url',
            method: 'get',
            timeout: 10000,
            isArray: true
        }

    });
}

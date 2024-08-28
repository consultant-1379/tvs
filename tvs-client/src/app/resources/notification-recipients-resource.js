export default function notificationRecipientsResource(resourceFactory, tvsRoot) {
    'ngInject';

    return resourceFactory.create(tvsRoot,
        '/job-notifications/:jobId/recipients/:recipientId',
        {recipientId: '@recipientId'}
    );
}

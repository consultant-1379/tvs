export default class NotificationsController {
    constructor($state,
                $stateParams,
                notificationRulesResource,
                notificationRecipientsResource,
                jobsResource,
                tvsTableService) {
        'ngInject';

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.rulesResource = notificationRulesResource;
        this.recipientsResource = notificationRecipientsResource;
        this.jobsResource = jobsResource;
        this.tvsTableService = tvsTableService;

        this.jobId = $stateParams.jobId;
        this.rules = [];
        this.fields = [];
        this.requiredKeys = ['field', 'fieldType', 'operation', 'value'];
        this.recipients = [];
        this.emailRegex = '[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*' +
            '[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?';

        this.loadRules();
        this.loadRecipients();
        this.loadJob();
    }

    loadRules() {
        this.rulesResource.query({
            jobId: this.jobId
        }).$promise
            .then((response) => {
                this.rules = response.data;
                this.fields = response.meta.columns;
            });
    }

    updateRule(rule, oldRule) {
        rule.fieldType = this.getFieldType(rule.field);
        if (oldRule && oldRule.fieldType !== rule.fieldType) {
            rule.value = undefined;
        }

        let operations = this.getOperations(rule);
        if (!_.includes(operations, rule.operation)) {
            rule.operation = operations[0];
        }

        if (this.isRuleValid(rule)) {
            if (_.has(rule, 'id')) {
                this.rulesResource.update({jobId: this.jobId, ruleId: rule.id}, rule);
            } else {
                this.rulesResource.create({jobId: this.jobId}, rule).$promise
                    .then(response => {
                        rule.id = response.data.id;
                    });
            }
        }
    }

    addRule() {
        this.rules.push({});
    }

    removeRule(rule) {
        if (_.has(rule, 'id')) {
            this.rulesResource.delete({jobId: this.jobId, ruleId: rule.id}).$promise
                .then(response => {
                    _.pull(this.rules, rule);
                });
        } else {
            _.pull(this.rules, rule);
        }
    }

    isRuleValid(rule) {
        return this.requiredKeys.every(key => _.get(rule, key));
    }

    getOperations(rule) {
        let defaultSet = ['=', '<', '>'];
        let columnType = rule.fieldType;
        switch (columnType) {
            case 'date':
            case 'duration':
            case 'number':
                return defaultSet;
            case 'result':
            case 'string':
            default:
                return ['~'];
        }
    }

    getFieldType(field) {
        let type = _.find(this.fields, {field}).type;
        return this.tvsTableService.getColumnType(type);
    }

    loadRecipients() {
        this.recipientsResource.query({
            jobId: this.jobId
        }).$promise
            .then((response) => {
                this.recipients = response.data;
            });
    }

    addRecipient(recipient) {
        this.recipientsResource.create({jobId: this.jobId}, recipient);
    }

    removeRecipient(recipient) {
        this.recipientsResource.delete({jobId: this.jobId, recipientId: recipient.email});
    }

    loadJob() {
        return this.jobsResource.query({
            id: this.jobId
        }).$promise
            .then(job => {
                this.job = job.data;
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }
}

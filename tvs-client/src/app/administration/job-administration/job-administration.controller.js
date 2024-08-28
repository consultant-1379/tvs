export default class JobAdministrationController {
    constructor($q,
                $state,
                $stateParams,
                jobsResource,
                jobsInsertResource,
                contextResource,
                tmsGridService,
                tvsTableService) {
        'ngInject';

        this.$q = $q;
        this.$state = $state;
        this.$stateParams = $stateParams;
        this.jobsResource = jobsResource;
        this.jobsInsertResource = jobsInsertResource;
        this.contextResource = contextResource;
        this.tmsGridService = tmsGridService;
        this.tvsTableService = tvsTableService;

        this.contexts = [];

        this.configureColumns();
    }

    configureColumns() {

        this.predefinedColumns = {
            'id': {
                disabled: true
            },
            'modifiedDate': {
                disabled: true
            },
            'context': {
                disabled: true
            },
            'contextNode.name': {
                sortingDisabled: true,
                field: 'contextNode.name',
                title: 'Context',
                type: 'Select',
                additional: {
                    items: () => this.contexts,
                    onSelect: ({name, context}, newContext) => {
                        this.jobsInsertResource.create({
                            jobName: name,
                            contextId: context
                        }, {
                            context: newContext.id,
                            testSessions: []
                        });
                    }
                },
                filter: false
            },
            'testSessionCount': {
                columnType: 'number',
                show: false
            },
            'lastTestSessionTestSuiteCount': {
                columnType: 'number',
                show: false
            },
            'lastTestSessionTestCaseCount': {
                columnType: 'number',
                show: false
            },
            'lastExecutionDate': {
                show: false
            },
            'lastTestSessionDuration': {
                show: false
            },
            'avgTestSessionDuration': {
                show: false
            }
        };
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.$q.all({
            jobs: this.loadJobs(page, size, orderBy, orderMode, q),
            contexts: this.contextResource.query().$promise
        }).then(({jobs, contexts}) => {
            this.contexts = contexts.data;
            _.forEach(jobs.data, job => {
                job.contextNode = _.find(contexts.data, item => item.id === job.context);
            });
            return jobs;
        });
    }

    loadJobs(page, size, orderBy, orderMode, q) {
        return this.jobsResource.query({page, size, orderBy, orderMode, q}).$promise;
    }

    navigateToJob({id: jobId}) {
        this.$state.go('job', {jobId});
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    getStorageKey() {
        return 'tvs.jobs.grid.columns';
    }

}

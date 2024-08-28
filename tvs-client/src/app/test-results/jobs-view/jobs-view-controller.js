export default class JobsViewController {
    constructor($q,
                $state,
                $stateParams,
                jobsResource,
                contextSelectorService,
                contextResource,
                tmsGridService,
                tvsTableService,
                jobsTooltips) {
        'ngInject';

        this.$q = $q;
        this.$state = $state;
        this.$stateParams = $stateParams;
        this.jobsResource = jobsResource;
        this.contextSelectorService = contextSelectorService;
        this.contextResource = contextResource;
        this.tmsGridService = tmsGridService;
        this.tvsTableService = tvsTableService;
        this.jobsTooltips = jobsTooltips;
        this.context = null;

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
                type: 'String',
                filter: false,
                show: false,
                tooltip: this.jobsTooltips.context
            },
            'name': {
                position: 0,
                tooltip: this.jobsTooltips.name
            },
            'testSessionCount': {
                position: 1,
                tooltip: this.jobsTooltips.testSessionCount
            },
            'lastTestSessionTestSuiteCount': {
                position: 2,
                tooltip: this.jobsTooltips.lastTestSessionTestSuiteCount
            },
            'lastTestSessionTestCaseCount': {
                position: 3,
                tooltip: this.jobsTooltips.lastTestSessionTestCaseCount
            },
            'lastExecutionDate': {
                position: 4,
                tooltip: this.jobsTooltips.lastExecutionDate
            },
            'lastTestSessionDuration': {
                show: false,
                tooltip: this.jobsTooltips.lastTestSessionDuration
            },
            'avgTestSessionDuration': {
                position: 5,
                tooltip: this.jobsTooltips.avgTestSessionDuration
            }
        };
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.$q.all({
            jobs: this.loadJobs(page, size, orderBy, orderMode, q),
            contexts: this.contextResource.query().$promise
        }).then(({jobs, contexts}) => {
            _.forEach(jobs.data, job => {
                job.contextNode = _.find(contexts.data, item => item.id === job.context);
            });
            return jobs;
        });
    }

    loadJobs(page, size, orderBy, orderMode, q) {
        return this.restoreContext().then(context => {
            let contextId = _.get(context, 'id');
            return this.jobsResource.query({contextId, page, size, orderBy, orderMode, q}).$promise;
        });
    }

    navigateToJob(row) {
        let contextId = _.get(this.context, 'id');
        this.$state.go('job', {jobId: row.id, contextId});
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    getStorageKey() {
        return 'tvs.jobs.grid.columns';
    }

    restoreContext() {
        return this.resolveContextSelection(this.contextSelectorService.restore());
    }

    selectContext() {
        return this.resolveContextSelection(this.contextSelectorService.openAside())
            .then(context => {
                let contextId = _.get(context, 'id');
                this.$state.go('.', {contextId, page: 1}, {reload: true});
            });
    }

    resolveContextSelection(contextPromise) {
        return contextPromise
            .then(context => {
                this.context = context;
                return context;
            });
    }

}

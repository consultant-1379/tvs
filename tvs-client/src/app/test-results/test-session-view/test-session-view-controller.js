export default class TestSessionViewController {
    constructor($state,
                $stateParams,
                contextResource,
                testSuiteResultsResource,
                testSessionsResource,
                jobsResource,
                contextSelectorService,
                tmsGridService,
                tvsTableService,
                errorReporter,
                modelService,
                testSuiteTooltips) {
        'ngInject';

        this.contextResource = contextResource;
        this.testSuiteResultsResource = testSuiteResultsResource;
        this.testSessionsResource = testSessionsResource;
        this.jobsResource = jobsResource;
        this.contextSelectorService = contextSelectorService;
        this.tmsGridService = tmsGridService;
        this.tvsTableService = tvsTableService;
        this.modelService = modelService;
        this.$state = $state;
        this.$stateParams = $stateParams;
        this.testSuiteTooltips = testSuiteTooltips;

        this.jobId = $stateParams.jobId;
        this.executionId = $stateParams.executionId;

        this.errorReporter = errorReporter;
        this.portletFields = [];
        this.portletTestSession = {};

        this.loadJob();
        this.loadTestSession();
        this.configureColumns();
        this.configureJobPortlet();
    }

    loadJob() {
        this.jobsResource.query({id: this.jobId}).$promise
            .then(job => {
                this.job = job.data;
                this.portletJob = this.modelService.cleanCopy(this.job, key => {
                    return _.contains(['modifiedDate', 'testSessions'], key);
                });
                this.resolveContextName(this.job);
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    loadTestSession() {
        this.testSessionsResource.query({
            jobId: this.jobId,
            id: this.executionId
        }).$promise
            .then(testSession => {
                this.testSession = testSession.data;
                this.configureTestSessionPortlet();
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    configureColumns() {
        this.predefinedColumns =
        {
            name: {
                position: 0,
                tooltip: this.testSuiteTooltips.name
            },
            'time.duration': {
                type: 'Duration',
                position: 1,
                tooltip: this.testSuiteTooltips.duration
            },
            'time.startDate': {
                show: false,
                tooltip: this.testSuiteTooltips.startDate
            },
            'time.stopDate': {
                show: false,
                tooltip: this.testSuiteTooltips.stopDate
            },
            'passRate': {
                position: 8,
                tooltip: this.testSuiteTooltips.passRate
            },
            'createdDate': {
                show: false,
                tooltip: this.testSuiteTooltips.createdDate
            },
            'modifiedDate': {
                show: false
            },
            'statistics.total': {
                position: 2,
                tooltip: this.testSuiteTooltips.total
            },
            'statistics.passed': {
                position: 3,
                tooltip: this.testSuiteTooltips.passed
            },
            'statistics.pending': {
                position: 4,
                tooltip: this.testSuiteTooltips.pending
            },
            'statistics.cancelled': {
                position: 5,
                tooltip: this.testSuiteTooltips.cancelled
            },
            'statistics.failed': {
                position: 6,
                tooltip: this.testSuiteTooltips.failed
            },
            'statistics.broken': {
                position: 7,
                tooltip: this.testSuiteTooltips.broken
            }
        };
    }

    configureTestSessionPortlet() {
        this.portletTestSession = this.modelService.cleanCopy(this.testSession, key => {
            return _.contains(['modifiedDate', 'createdDate', 'testSuites'], key);
        });

        this.portletFields = [
            {
                key: 'time.startDate',
                name: 'Started',
                filter: 'tmsDateTime'
            }, {
                key: 'time.stopDate',
                name: 'Stopped',
                filter: 'tmsDateTime'
            }, {
                key: 'time.duration',
                name: 'Duration',
                filter: 'tmsDuration'
            }, {
                key: 'uri',
                filter: 'linky:\'_blank\''
            }, {
                key: 'lastExecutionDate',
                filter: 'tmsDateTime'
            }, {
                key: 'passRate',
                filter: x => `${x}%`
            }
        ];
    }

    configureJobPortlet() {
        this.portletJobFields = [
            {
                key: 'context',
                value: this.contextName
            }, {
                key: 'lastExecutionDate',
                filter: 'tmsDateTime'
            }, {
                key: 'lastTestSessionDuration',
                filter: 'tmsDuration'
            }, {
                key: 'avgTestSessionDuration',
                filter: 'tmsDuration'
            }
        ];
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.testSuiteResultsResource.query({
            jobId: this.jobId,
            executionId: this.executionId,
            page, size, orderBy, orderMode, q
        }).$promise;
    }

    navigateToSuiteResult(row) {
        this.$state.go('testSuiteResult', {
            jobId: this.jobId,
            executionId: this.executionId,
            testSuiteName: row.id
        });
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    getStorageKey() {
        return 'tvs.test-session.grid.columns';
    }

    resolveContextName(job) {
        this.contextResource.query({id: _.get(job, 'context')}).$promise
            .then(context => {
                this.contextName = context.data.name;
                this.configureJobPortlet();
            });
    }

}

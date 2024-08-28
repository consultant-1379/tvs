export default class TestExecutionFlakinessViewController {
    constructor($state,
                $stateParams,
                $window,
                testCaseExecutionHistoryResource,
                errorReporter,
                tvsTableService,
                jobsResource,
                modelService,
                contextResource,
                tableParamsFactory) {
        'ngInject';

        this.testCaseExecutionHistoryResource = testCaseExecutionHistoryResource;
        this.tvsTableService = tvsTableService;
        this.jobsResource = jobsResource;
        this.modelService = modelService;
        this.contextResource = contextResource;
        this.tableParamsFactory = tableParamsFactory;

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.$window = $window;

        this.time = {
            start: $stateParams.startTime,
            stop: $stateParams.endTime
        };
        this.testCaseId = $stateParams.testCaseId;
        this.job = {id: this.$stateParams.jobId};
        this.fetchData();

        this.portletFields = [];
        this.portletFlakinessFields = [];
        this.loadJob();
        this.configurePortlet();
        this.configurePortletFlakiness();
        this.portletJob = {};
        this.portletFlakiness = {};
        this.configureTestFlakinessPortlet();

        this.errorReporter = errorReporter;

        this.configureColumns();
    }

    configureColumns() {
        this.predefinedColumns = {
            'resultCode': {
                title: 'Status',
                field: 'resultCode',
                type: 'Status',
                show: true
            },
            'createdDate': {
                title: 'Created Date',
                field: 'createdDate',
                type: 'DateTime',
                show: false
            },
            'time.startDate': {
                title: 'Start Date',
                field: 'time.startDate',
                type: 'DateTime',
                show: true
            },
            'time.stopDate': {
                title: 'End Date',
                field: 'time.stopDate',
                type: 'DateTime',
                show: true
            },
            'time.duration': {
                title: 'Duration',
                field: 'time.duration',
                type: 'Duration',
                show: true
            },
            'executionId': {
                title: 'Session',
                field: 'executionId',
                navigate: (args) => this.navigateToTestSession(args),
                type: 'InnerLink',
                show: true,
                filter: false
            },
            'testSuiteName': {
                title: 'Suite',
                field: 'testSuiteName',
                navigate: (args) => this.navigateToTestSuite(args),
                type: 'InnerLink',
                show: true,
                filter: false
            }
        };
    }

    loadJob() {
        this.jobsResource.query({
            id: _.get(this.job, 'id')
        }).$promise
            .then(job => {
                this.job = job.data;
                this.portletJob = this.modelService.cleanCopy(this.job, key => {
                    return _.contains(['testSessions', 'modifiedDate'], key);
                });
                this.resolveContextName(this.job);
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    resolveContextName(job) {
        this.contextResource.query({id: _.get(this.job, 'context')}).$promise
            .then(context => {
                this.contextName = context.data.name;
                this.configurePortlet();
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    navigateToTestSession({jobId, executionId}) {
        return this.$state.go('testSession', {
            jobId,
            executionId
        });
    }

    navigateToTestSuite({jobId, executionId, testSuiteName}) {
        return this.$state.go('testSuiteResult', {
            jobId,
            executionId,
            testSuiteName
        });
    }

    configurePortlet() {
        this.portletFields = [
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

    configurePortletFlakiness() {
        this.portletFlakinessFields = [
            {
                key: 'flakiness',
                name: 'Test Flakiness Result',
                filter: x => `${x}%`
            }, {
                key: 'executionCount',
                name: 'Number Of Executions'
            }, {
                key: 'executionResult.passed',
                name: 'Number Of Passing Tests'
            }, {
                key: 'executionResult.failed',
                name: 'Number Of Failing Tests'
            }, {
                key: 'executionResult.broken',
                name: 'Number Of Broken Tests'
            }, {
                key: 'executionResult.cancelled',
                name: 'Number Of Cancelled Tests'
            }
        ];
    }

    configureTestFlakinessPortlet() {
        let startTime = this.time.start;
        let endTime = this.time.stop;

        this.jobsResource.getSingleTestCaseFlakiness({
            id: this.job.id,
            testCaseId: this.testCaseId,
            startTime,
            endTime
        }).$promise
            .then(testFlakiness => {
                this.portletFlakiness = this.createFlakinessPortletObject(testFlakiness.data);
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    createFlakinessPortletObject(testFlakiness) {
        let testCase = {};
        testCase.flakiness = testFlakiness.flakiness;
        testCase.executionCount = Object.keys(testFlakiness.latestResults).length;
        testCase.executionResult = this.computeResultsBreakdown(testFlakiness);
        return testCase;
    }

    computeResultsBreakdown(testFlakiness) {
        let executionResult = {};
        executionResult.passed = 0;
        executionResult.failed = 0;
        executionResult.broken = 0;
        executionResult.cancelled = 0;

        _.forEach(testFlakiness.latestResults, function(result) {
            if (result.resultCode === 'PASSED') {
                executionResult.passed++;
            } else if (result.resultCode === 'FAILED') {
                executionResult.failed++;
            } else if (result.resultCode === 'BROKEN') {
                executionResult.broken++;
            } else if (result.resultCode === 'CANCELLED') {
                executionResult.cancelled++;
            }
        });

        executionResult = {
            passed: executionResult.passed,
            failed: executionResult.failed,
            broken: executionResult.broken,
            cancelled: executionResult.cancelled
        };
        return executionResult;
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    fetchData(page, size, orderBy, orderMode, q) {
        let jobId = this.job.id;
        let startTime = this.time.start;
        let stopTime = this.time.stop;
        this.buildQuery(jobId);
        q = this.query;

        return this.testCaseExecutionHistoryResource.query({
            testCaseId: this.testCaseId, startTime, stopTime,
            page, size, orderBy, orderMode, q
        }).$promise;
    }

    buildQuery(jobId) {
        let opts = {q: ''};
        let filter = {
            filter() {
                return {
                    jobId: {value: jobId}
                };
            }
        };
        this.tableParamsFactory.applyFiltering(opts, filter);
        this.query = opts.q;
    }
}

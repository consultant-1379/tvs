export default class TestSuiteViewController {
    constructor($state,
                $stateParams,
                jobsResource,
                contextResource,
                testSessionsResource,
                testSuiteResultsResource,
                testCaseResultsResource,
                tmsGridService,
                $window,
                endpointLinkFactory,
                errorReporter,
                tvsTableService,
                modelService,
                statusService,
                testCaseTooltips) {
        'ngInject';

        this.tmsGridService = tmsGridService;
        this.testSuiteResultsResource = testSuiteResultsResource;
        this.testCaseResultsResource = testCaseResultsResource;
        this.endpointLinkFactory = endpointLinkFactory;
        this.modelService = modelService;
        this.jobsResource = jobsResource;
        this.contextResource = contextResource;
        this.testSessionsResource = testSessionsResource;
        this.tvsTableService = tvsTableService;
        this.statusService = statusService;
        this.testCaseTooltips = testCaseTooltips;

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.$window = $window;

        this.jobId = $stateParams.jobId;
        this.executionId = $stateParams.executionId;
        this.testSuiteName = decodeURIComponent($stateParams.testSuiteName);

        this.errorReporter = errorReporter;
        this.portletJob = {};
        this.portletTestSession = {};
        this.jobFields = [];
        this.testSessionFields = [];

        this.loadJob();
        this.loadTestSession();
        this.loadTestSuite();
        this.configureColumns();
        this.setupChart();
    }

    loadTestSuite() {
        this.testSuiteResultsResource.query({
            jobId: this.jobId,
            executionId: this.executionId,
            name: this.testSuiteName
        }).$promise
            .then(testSuiteResult => {
                this.testSuiteResult = testSuiteResult.data;
                this.updateChartData(this.testSuiteResult);
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    loadJob() {
        this.jobsResource.query({id: this.jobId}).$promise
            .then(job => {
                this.job = job.data;
                this.loadContext(this.job.context);
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    loadTestSession() {
        this.testSessionsResource.query({jobId: this.jobId, id: this.executionId}).$promise
            .then(testSession => {
                this.testSession = testSession.data;
                this.configureTestSessionPortlet();
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    loadContext(contextId) {
        this.contextResource.query({id: contextId}).$promise
            .then(context => {
                this.contextName = context.data.name;
                this.configureJobPortlet();
            });
    }

    configureColumns() {
        this.predefinedColumns = {
            'id': {
                disabled: true
            },
            'TITLE': {
                show: false,
                position: 0,
                tooltip: this.testCaseTooltips.title
            },
            'name': {
                position: 1,
                tooltip: this.testCaseTooltips.name
            },
            'time.duration': {
                position: 2,
                tooltip: this.testCaseTooltips.duration
            },
            'time.startDate': {
                show: false,
                tooltip: this.testCaseTooltips.startDate
            },
            'time.stopDate': {
                show: false,
                tooltip: this.testCaseTooltips.stopDate
            },
            'resultCode': {
                position: 3,
                tooltip: this.testCaseTooltips.resultCode
            },
            'REQUIREMENTS': {
                position: 4,
                tooltip: this.testCaseTooltips.requirements
            },
            'GROUPS': {
                position: 5,
                tooltip: this.testCaseTooltips.groups
            },
            'PRIORITY': {
                type: 'StringShort',
                position: 6,
                tooltip: this.testCaseTooltips.priority
            },
            'COMPONENTS': {
                position: 7,
                tooltip: this.testCaseTooltips.component
            },
            createdDate: {
                show: false,
                tooltip: this.testCaseTooltips.createdDate
            },
            modifiedDate: {
                show: false,
                tooltip: this.testCaseTooltips.modifiedDate
            },
            'ISO_VERSION': {
                type: 'StringShort',
                show: false,
                tooltip: this.testCaseTooltips.isoVersion
            },
            'DROP_NAME': {
                type: 'StringShort',
                show: false,
                tooltip: this.testCaseTooltips.dropName
            }
        };
    }

    setupChart() {
        this.chartData = [
            {key: 'Passed', value: 0, color: '#89ba17'},
            {key: 'Failed', value: 0, color: '#e32119'},
            {key: 'Broken', value: 0, color: '#f08a00'},
            {key: 'Cancelled', value: 0, color: '#fabb00'}
        ];
        this.chartOptions = {
            chart: {
                type: 'pieChart',
                height: 250,
                x: d => d.key,
                y: d => d.value,
                showLabels: false,
                valueFormat: d => this.$window.d3.format('d')(d),
                duration: 500,
                labelThreshold: 0.01
            }
        };
    }

    updateChartData(testSuite) {
        let stats = _.get(testSuite, 'statistics');

        _.forEach(this.chartData, obj => {
            let status = obj.key.toLowerCase();
            obj.value = stats[status];
        });
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.testCaseResultsResource.query({
            jobId: this.jobId,
            executionId: this.executionId,
            testSuiteName: this.testSuiteName,
            page, size, orderBy, orderMode, q
        }).$promise;
    }

    navigateToTestCaseExecutionHistory(row) {
        this.$state.go('executionHistory', {testCaseId: row.name});
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    getStorageKey() {
        return 'tvs.test-suite-result.grid.columns';
    }

    configureJobPortlet() {
        this.portletJob = this.modelService.cleanCopy(this.job, key => {
            return _.contains(['modifiedDate', 'testSessions'], key);
        });

        this.jobFields = [
            {
                key: 'lastExecutionDate',
                filter: 'tmsDateTime'
            }, {
                key: 'context',
                value: this.contextName
            }, {
                key: 'lastTestSessionDuration',
                filter: 'tmsDuration'
            }, {
                key: 'avgTestSessionDuration',
                filter: 'tmsDuration'
            }
        ];
    }

    configureTestSessionPortlet() {
        this.portletTestSession = this.modelService.cleanCopy(this.testSession, key => {
            return _.contains(['modifiedDate', 'createdDate', 'testSuites'], key);
        });

        this.testSessionFields = [
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

}

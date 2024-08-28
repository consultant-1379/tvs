export default class TestCaseExecutionHistoryViewController {
    constructor($state,
                $stateParams,
                $window,
                endpointLinkFactory,
                tmsGridService,
                testCaseExecutionHistoryResource,
                errorReporter,
                tvsTableService,
                statusService) {
        'ngInject';

        this.endpointLinkFactory = endpointLinkFactory;
        this.tmsGridService = tmsGridService;
        this.testCaseExecutionHistoryResource = testCaseExecutionHistoryResource;
        this.tvsTableService = tvsTableService;
        this.statusService = statusService;

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.$window = $window;

        this.testCaseId = $stateParams.testCaseId;

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
            'jobId': {
                title: 'Test Activity',
                field: 'jobId',
                navigate: (args) => this.navigateToJob(args),
                type: 'InnerLink',
                show: true,
                filter: false
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

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.testCaseExecutionHistoryResource.query({
            testCaseId: this.testCaseId,
            page, size, orderBy, orderMode, q
        }).$promise;
    }

    getStorageKey() {
        return 'tvs.test-case-execution-history.grid.columns';
    }

    navigateToJob({jobId}) {
        return this.$state.go('job', {
            jobId
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

    navigateToTestCaseDetails(testCaseId) {
        this.$window.open(this.endpointLinkFactory.namTestCase(testCaseId), '_blank');
    }

}

export default class TestFlakinessController {
    constructor($state,
                $stateParams,
                $q,
                jobsResource,
                testSessionsResource,
                moment,
                dateTimeFormat,
                errorReporter) {
        'ngInject';

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.$q = $q;
        this.jobsResource = jobsResource;
        this.testSessionsResource = testSessionsResource;
        this.errorReporter = errorReporter;

        this.job = {id: this.$stateParams.jobId};
        this.maxSessions = 100;

        this.moment = moment;
        this.dateTimeFormat = dateTimeFormat;
        this.fromDate = moment().subtract(1, 'months').format(dateTimeFormat.DAY);
        this.toDate = moment().format(dateTimeFormat.DAY);

        this.loadJob();
        this.reloadReport();

        this.onFilterChanged = _.debounce(this.reloadReport, 1000);
    }

    loadJob() {
        return this.jobsResource.query({
            id: this.job.id
        }).$promise
            .then(job => {
                this.job = job.data;
            })
            .catch(error => {
                this.errorReporter.reportNotFound();
            });
    }

    reloadReport() {
        if (!this.fromDate || !this.toDate) {
            return;
        }
        let toDate = this.moment(this.toDate).add(1, 'days').format(this.dateTimeFormat.DAY);

        this.time = {
            start: this.fromDate,
            stop: toDate
        };

        let flakiness = this.loadFlakiness(this.fromDate, toDate);
        let sessions = this.loadSessions(this.fromDate, toDate);

        this.report = null;

        this.$q.all({flakiness, sessions})
            .then(all => {
                let sessionsToDisplay = all.sessions.data.slice(0, 10);
                this.report = {
                    testCases: all.flakiness.data,
                    sessions: sessionsToDisplay.reverse()
                };
            });
    }

    navigateToTestSessions(testCase) {
        this.$state.go('flakinessExecutionHistory', {
            jobId: this.job.id,
            testCaseId: testCase.id,
            startTime: this.time.start,
            endTime: this.time.stop
        });
    }

    loadFlakiness(startTime, endTime) {
        return this.jobsResource.getFlakiness({
            id: this.job.id,
            startTime,
            endTime
        }).$promise;
    }

    getExecutionCount(executions) {
        return Object.keys(executions).length;
    }

    loadSessions(dateFrom, dateTo) {
        return this.testSessionsResource.query({
            jobId: this.job.id,
            startTime: dateFrom,
            endTime: dateTo,
            size: this.maxSessions
        }).$promise;
    }

    getTooltipText(result) {
        if (result != null) {
            return `Latest status: ${_.startCase(result.resultCode)}<br> Execution count: ${result.executionCount}`;
        }
        return null;
    }
}

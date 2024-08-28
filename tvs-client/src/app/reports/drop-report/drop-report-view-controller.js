export default class DropReportViewController {
    constructor($state,
                $stateParams,
                jobsResource,
                tvsTableService,
                errorReporter,
                moment,
                dateTimeFormat,
                reportsResource,
                dropReportTooltips) {
        'ngInject';
        this.$state = $state;
        this.$stateParams = $stateParams;
        this.jobsResource = jobsResource;
        this.errorReporter = errorReporter;
        this.tvsTableService = tvsTableService;
        this.moment = moment;
        this.dateTimeFormat = dateTimeFormat;
        this.reportsResource = reportsResource;
        this.dropReportTooltips = dropReportTooltips;

        this.job = {id: this.$stateParams.jobId};

        this.reportTypes = [{
            key: 'LAST_ISO',
            value: 'Last ISO'
        }, {
            key: 'CUMULATIVE',
            value: 'Cumulative'
        }];
        this.selectedReportType = _.find(this.reportTypes, 'key', $stateParams.reportType) || this.reportTypes[0];

        this.dateTimeFormat = dateTimeFormat;
        this.fromDate = moment().subtract(1, 'years').format(dateTimeFormat.DAY);
        this.toDate = moment().format(dateTimeFormat.DAY);

        this.loadJob();
        this.configureChart();
        this.configureColumns();

        this.onFilterChanged = _.debounce(() => this.tableControlObject.reload(), 1000);
    }

    reloadChart(q) {
        if (!this.fromDate || !this.toDate) {
            return;
        }
        let toDate = this.moment(this.toDate).add(1, 'days').format(this.dateTimeFormat.DAY);
        this.$state.go('.', {reportType: this.selectedReportType.key});

        this.reportsResource.query({
            jobId: this.job.id,
            id: 'drop-report-chart',
            type: this.selectedReportType.key,
            since: this.fromDate,
            until: toDate,
            q
        }).$promise.then(data => {
            this.configureChartData(data.data);
        });
    }

    configureColumns() {
        this.predefinedColumns = {
            dropName: {
                field: 'dropName',
                title: 'Drop',
                tooltip: this.dropReportTooltips.dropName,
                type: 'String',
                context: null
            },
            latestIsoVersion: {
                field: 'latestIsoVersion',
                title: 'Last ISO Version',
                tooltip: this.dropReportTooltips.latestIsoVersion,
                type: 'String',
                context: null
            },
            passRate: {
                field: 'passRate',
                title: 'Pass Rate',
                tooltip: this.dropReportTooltips.passRate,
                type: 'Rate',
                context: null
            },
            testCasesCount: {
                field: 'testCasesCount',
                title: 'Test Cases',
                tooltip: this.dropReportTooltips.testCasesCount,
                type: 'NumberLong',
                context: null
            },
            passedTestCaseCount: {
                field: 'passedTestCaseCount',
                title: 'Passed Test Cases',
                tooltip: this.dropReportTooltips.passedTestCaseCount,
                show: false,
                type: 'NumberLong',
                context: null
            },
            failedTestCaseCount: {
                field: 'failedTestCaseCount',
                title: 'Failed Test Cases',
                tooltip: this.dropReportTooltips.failedTestCaseCount,
                show: false,
                type: 'NumberLong',
                context: null
            },
            testSuitesCount: {
                field: 'testSuitesCount',
                title: 'Test Suites',
                tooltip: this.dropReportTooltips.testSuitesCount,
                type: 'NumberShort',
                context: null
            },
            testSessionsCount: {
                field: 'testSessionsCount',
                title: 'Test Sessions',
                tooltip: this.dropReportTooltips.testSessionsCount,
                type: 'NumberLong',
                context: null
            },
            isoLastStartTime: {
                field: 'isoLastStartTime',
                title: 'Last Execution Time',
                tooltip: this.dropReportTooltips.isoLastStartTime,
                type: 'DateTime',
                context: null
            }
        };
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

    configureChart() {
        this.chartOptions = {
            chart: {
                margin: {
                    right: 40
                },
                maxKeyLength: 200,
                type: 'multiChart',
                height: 250,
                duration: 100,
                interpolate: 'linear',
                xAxis: {
                    axisLabel: 'Drop',
                    showMaxMin: false,
                    tickFormat: d => {
                        let value = this.chartData[0].values[d];
                        if (value) {
                            return value.drop;
                        }
                        return null;
                    }
                },
                yAxis1: {
                    showMaxMin: true,
                    tickFormat: d => {
                        return d;
                    }
                },
                yAxis2: {
                    showMaxMin: false,
                    orient: 'right',
                    tickFormat: d => {
                        return `${d}%`;
                    }
                },
                yDomain2: [0, 100],
                tooltip: {
                    headerFormatter: d => {
                        let value = this.chartData[0].values[d];
                        if (value) {
                            let date = this.moment(value.date)
                                .format(`${this.dateTimeFormat.DATE} ${this.dateTimeFormat.TIME}`);
                            return `Drop: <span class="unbold">${value.drop}</span>
                            <br/>ISO: <span class="unbold">${value.iso}</span>
                            <br/><span class="unbold">${date}</span>`;
                        }
                        return null;
                    }
                }
            },
            title: {
                enable: false
            }
        };
        this.chartData = [];
    }

    configureChartData(data) {
        let totalValues = [];
        let passedValues = [];
        let passRateValues = [];

        let i = 0;
        this.maxY = 0;
        _.forEach(data, datum => {
            let drop = datum.id;
            let iso = datum.latestIsoVersion;
            let date = datum.isoLastStartTime;
            this.maxY = Math.max(this.maxY, datum.testCasesCount);
            totalValues.push({drop, iso, date, y: datum.testCasesCount, x: i});
            passedValues.push({drop, iso, date, y: Math.ceil((datum.testCasesCount * datum.passRate) / 100), x: i});
            passRateValues.push({drop, iso, date, y: datum.passRate, x: i++});
        });

        this.chartData = [
            {key: 'Total', values: totalValues, color: '#00285f', yAxis: 1, type: 'line'},
            {key: 'Passed', values: passedValues, color: '#89ba17', yAxis: 1, type: 'line'},
            {key: 'Pass Rate %', values: passRateValues, color: '#00a9d4', yAxis: 2, type: 'line'}
        ];
        this.chartOptions.chart.yDomain1 = [0, Math.ceil(this.maxY + this.maxY * 0.1)];
    }

    fetchData(page, size, orderBy, orderMode, q) {
        let jobId = this.job.id;
        let toDate = this.moment(this.toDate).add(1, 'days').format(this.dateTimeFormat.DAY);
        return this.reportsResource.query({
            jobId, id: 'drop-report-table',
            type: this.selectedReportType.key,
            since: this.fromDate,
            until: toDate, page, size, orderBy, orderMode, q
        }).$promise
            .then(data => {
                this.reloadChart(q);
                return data;
            });
    }

    navigate(row) {
    }

    computeColumns(data, meta) {
        this.configureColumns();
        let fields = _.uniq(_.flatten(_.map(data, single => _.map(single, (value, key) => key))));
        let a = _.filter(this.predefinedColumns, (col, key) => {
            col.field = key;
            return _.contains(fields, key);
        });

        return this.tvsTableService.generateColumns(a, meta.columns);
    }

    getStorageKey() {
        return `tvs.job.${this.job.id}.drop-report.${this.selectedReportType.key}.grid.columns`;
    }

}

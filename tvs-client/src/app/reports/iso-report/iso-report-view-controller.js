export default class IsoReportViewController {
    constructor($q,
                $scope,
                $state,
                $stateParams,
                contextSelectorService,
                contextReportsResource,
                jobsResource,
                tvsTableService,
                errorReporter,
                moment,
                dateTimeFormat,
                reportsResource,
                reportsService,
                isoReportTooltips) {
        'ngInject';
        this.$q = $q;
        this.$state = $state;
        this.$stateParams = $stateParams;
        this.contextReportsResource = contextReportsResource;
        this.contextSelectorService = contextSelectorService;
        this.errorReporter = errorReporter;
        this.tvsTableService = tvsTableService;
        this.reportsService = reportsService;
        this.isoReportTooltips = isoReportTooltips;
        this.allLabels = [];
        this.rawData = {};

        this.reportTypes = [{key: 'iso-priority', value: 'Test Run Priority'},
            {key: 'iso-component', value: 'Test Run Component'},
            {key: 'iso-group', value: 'Test Run Group'}];
        this.selectedReportType = _.find(this.reportTypes, 'key', $stateParams.reportType) || this.reportTypes[0];

        this.selectedIso = $stateParams.ISO || null;
        this.enabledLabels = [];
        this.enabledActivityLabels = [];
        this.jobNamesForFilter = '';
        this.activityLabelsEnabled = false;

        this.iso = [];

        this.configureChart();
        this.configureColumns();
        this.onFilterChanged = _.debounce(() => this.tableControlObject.reload(), 500);

        $scope.$watch('vm.selectedReportType', (reportType, old) => {
            if (old !== reportType) {
                this.enabledLabels = [];
                this.enabledActivityLabels = [];
                this.configureChart();
            }
        });
    }

    configureColumns() {
        this.predefinedColumns = {
            priority: {
                title: 'Priority',
                tooltip: this.isoReportTooltips.priority,
                type: 'String',
                context: null,
                filter: false,
                sortingDisabled: true
            },
            group: {
                title: 'Group',
                tooltip: this.isoReportTooltips.group,
                type: 'String',
                context: null,
                filter: false,
                sortingDisabled: true
            },
            component: {
                title: 'Component',
                tooltip: this.isoReportTooltips.component,
                type: 'String',
                context: null,
                filter: false,
                sortingDisabled: true
            },
            total: {
                title: 'Total',
                tooltip: this.isoReportTooltips.total,
                type: 'NumberShort',
                context: null,
                filter: false,
                sortingDisabled: true
            },
            passed: {
                title: 'Passed',
                tooltip: this.isoReportTooltips.passed,
                type: 'StatusSuccess',
                context: null,
                filter: false,
                sortingDisabled: true
            },
            failed: {
                title: 'Failed',
                tooltip: this.isoReportTooltips.failed,
                type: 'StatusBroken',
                context: null,
                filter: false,
                sortingDisabled: true
            },
            broken: {
                title: 'Broken',
                tooltip: this.isoReportTooltips.broken,
                type: 'StatusBroken',
                context: null,
                filter: false,
                sortingDisabled: true
            },
            cancelled: {
                title: 'Cancelled',
                tooltip: this.isoReportTooltips.cancelled,
                type: 'StatusCancelled',
                context: null,
                filter: false,
                sortingDisabled: true
            },
            passRate: {
                title: 'Pass Rate',
                tooltip: this.isoReportTooltips.passRate,
                type: 'Rate',
                context: null,
                filter: false,
                sortingDisabled: true
            }
        };
    }

    configureChart() {
        this.chartOptions = {
            chart: {
                type: this.selectedReportType.key === 'iso-priority' ? 'multiBarChart' : 'multiBarHorizontalChart',
                stacked: true,
                height: 250,
                duration: 300,
                xAxis: {
                    showMaxMin: false,
                    tickFormat: d => {
                        let value = this.chartData[0].values[d];
                        if (value) {
                            return value.xLabel;
                        }
                        return null;
                    }
                },
                yAxis: {
                    showMaxMin: true,
                    tickFormat: d => {
                        return d;
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
        this.rawData = data;

        let fieldName = this.selectedReportType.key.substr(4);
        let grouped = _.groupBy(data, fieldName);

        let passedValues = [];
        let failedValues = [];
        let brokenValues = [];
        let cancelledValues = [];

        let i = 0;
        let maxLabelLength = 0;

        _.forEach(grouped, datum => {
            let label = _.get(datum[0], fieldName);

            if (!_.contains(this.enabledLabels, label)) {
                return;
            }

            if (label && label.length > maxLabelLength) {
                maxLabelLength = label.length;
            }
            passedValues.push(this.extractChartDataPair(datum[0], 'passed', i, label));
            failedValues.push(this.extractChartDataPair(datum[0], 'failed', i, label));
            brokenValues.push(this.extractChartDataPair(datum[0], 'broken', i, label));
            cancelledValues.push(this.extractChartDataPair(datum[0], 'cancelled', i++, label));
        });

        this.chartData = [
            {key: 'Passed', values: passedValues, color: '#89ba17'},
            {key: 'Failed', values: failedValues, color: '#e32119'},
            {key: 'Broken', values: brokenValues, color: '#f08a00'},
            {key: 'Cancelled', values: cancelledValues, color: '#fabb00'}
        ];

        this.chartOptions.chart.height = 250 + i * 10;
        this.chartOptions.chart.margin = {
            left: 25 + maxLabelLength * 6
        };
        this.$state.go('.', {
            ISO: this.selectedIso,
            reportType: this.selectedReportType.key
        });
    }

    extractChartDataPair(datum, field, index, xLabel) {
        let value = _.get(datum, field) || 0;
        return {x: index, y: value, xLabel};
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.restoreContext()
            .then(context => {
                let contextId = context.id;
                if (this.enabledActivityLabels.length > 0) {
                    this.jobNamesForFilter = this.reportsService.findJobNamesForFilter(this.enabledActivityLabels);
                } else if (this.activityLabelsEnabled) {
                    this.jobNamesForFilter = null;
                }

                return this.contextReportsResource.query({
                    contextId, reportType: this.selectedReportType.key, iso: this.selectedIso,
                    jobNames: this.jobNamesForFilter, page, size, orderBy, orderMode
                }).$promise
                    .then(response => {
                        this.collectLabels(response.data);
                        this.configureChartData(response.data);
                        this.activityLabels = this.reportsService.collectTestActivityLabels(response.meta);
                        if (!this.activityLabelsEnabled) {
                            this.enabledActivityLabels = this.activityLabels;
                            this.activityLabelsEnabled = true;
                        }
                        this.populateIsoVersions(response.meta);
                        return response;
                    });
            });
    }

    populateIsoVersions(meta) {
        this.iso = _.get(meta, 'isoVersions');
        if (!this.selectedIso) {
            this.selectedIso = _.get(this.iso, '[0]');
        }
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
        return `tvs.context.${this.context.id}.iso-report.grid.columns.${this.selectedReportType.key}`;
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

    collectLabels(data) {
        this.allLabels = [];
        let fieldName = this.selectedReportType.key.substr(4);

        _.forEach(data, item => {
            this.allLabels.push(_.get(item, fieldName));
        });

        if (_.size(this.allLabels) > 10) {
            this.enabledLabels = _.clone(_.take(this.allLabels, 3));
        } else {
            this.enabledLabels = _.clone(this.allLabels);
        }
    }

    hideChart() {
        return _.size(this.allLabels) > 10 && _.size(this.enabledLabels) === 0;
    }

    hideTable() {
        return !this.selectedReportType || _.size(this.rawData) === 0;
    }
}

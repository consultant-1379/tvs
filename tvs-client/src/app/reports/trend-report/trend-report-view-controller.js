export default class TrendReportViewController {
    constructor($q,
                $scope,
                $state,
                $stateParams,
                contextSelectorService,
                tvsTableService,
                errorReporter,
                testCaseReportsResource,
                statusService,
                tmsGridService,
                tableFilterService,
                tableParamsFactory,
                reportsService,
                trendReportTooltips) {
        'ngInject';
        this.$q = $q;
        this.$scope = $scope;
        this.$state = $state;
        this.$stateParams = $stateParams;
        this.contextSelectorService = contextSelectorService;
        this.errorReporter = errorReporter;
        this.tvsTableService = tvsTableService;
        this.tmsGridService = tmsGridService;
        this.tableFilterService = tableFilterService;
        this.reportsService = reportsService;
        this.statusService = statusService;
        this.testCaseReportsResource = testCaseReportsResource;
        this.tableParamsFactory = tableParamsFactory;
        this.trendReportTooltips = trendReportTooltips;

        this.reportTypes = [
            {key: 'trend-priority', value: 'Test Case Priority'},
            {key: 'trend-component', value: 'Test Case Component'},
            {key: 'trend-group', value: 'Test Case Group'},
            {key: 'trend-suite', value: 'Test Case Suite'}
        ];
        this.selectedReportType = _.find(this.reportTypes, 'key', $stateParams.reportType) || this.reportTypes[0];
        this.initIsoSelection($stateParams);

        this.topNList = [5, 10, 15, 20, 25, 30];

        this.initResultCodeFilter($stateParams.resultCode);

        this.dropIsoMap = {};
        this.selectedDropIsoVersions = {};
        this.tableIso = [];
        this.tableData = {};
        this.rows = [];
        this.enabledActivityLabels = [];
        this.activityLabelsEnabled = false;
        this.jobNamesForFilter = '';

        this.configureChart();
        this.onFilterChanged = _.debounce(() => {
            this.refreshData();
        }, 500);

        this.refreshData();
    }

    computeColumns(data, meta) {
        this.selectedDropIsoVersions = {};
        let columns = [];

        columns.push(this.tmsGridService.createColumnObject({
            title: 'ISO',
            field: 'group',
            position: columns.length,
            formatFilter: null,
            show: true,
            context: null,
            columnType: 'string',
            additional: {
                vm: this
            }
        }));

        _.forEach(this.tableIso, iso => {
            columns.push(this.addNumericFilter(this.tmsGridService.createColumnObject({
                title: iso,
                field: iso,
                tooltip: this.trendReportTooltips.isoVersion,
                position: columns.length,
                formatFilter: {formatPercent: this.formatPercent, getColor: this.getColor},
                show: true,
                context: null,
                type: 'rate',
                columnType: 'number'
            })));
        });

        columns.push(this.addNumericFilter(this.tmsGridService.createColumnObject({
            title: 'Runs Passed',
            field: 'passed',
            tooltip: this.trendReportTooltips.passed,
            position: columns.length,
            formatFilter: null,
            show: true,
            context: null,
            type: 'rate',
            columnType: 'number'
        })));

        columns.push(this.addNumericFilter(this.tmsGridService.createColumnObject({
            title: 'Runs Failed',
            field: 'failed',
            tooltip: this.trendReportTooltips.failed,
            position: columns.length,
            formatFilter: null,
            show: true,
            context: null,
            type: 'number',
            columnType: 'number'
        })));

        this.columns = columns;
        return columns;
    }

    addNumericFilter(columnObject) {
        let filterObj = {};
        filterObj[columnObject.field] = `app/reports/trend-report/table/filters/number.html`;
        columnObject.filter = filterObj;
        return columnObject;
    }

    computeRows() {
        this.rows = [];
        _.forEach(this.getSortedData(), rates => {
            this.rows.push(this.computeRow(rates));
        });
    }

    computeRow(rates) {
        let row = {
            group: rates.group,
            passed: 0,
            failed: 0
        };

        _.forEach(this.tableIso, iso => {
            let rate = rates[iso];
            row[iso] = rate;
            if (rate || rate === 0) {
                if (rate === 100) {
                    row.passed++;
                } else {
                    row.failed++;
                }
            }
        });
        return row;
    }

    fetchData(page, size, orderBy, orderMode, q) {
        let data = {
            data: this.tableFilterService.filterRows(this.rows, q, emptyValueMapper),
            meta: {
                totalItems: this.rows.length
            }
        };

        function emptyValueMapper(value, type) {
            return (value == null && type === 'number') ? 100 : value;
        }

        return Promise.resolve(data);
    }

    refreshData() {
        this.loading = true;
        this.buildQuery();
        if (this.enabledActivityLabels.length > 0) {
            this.jobNamesForFilter = this.reportsService.findJobNamesForFilter(this.enabledActivityLabels);
        } else if (this.activityLabelsEnabled) {
            this.jobNamesForFilter = null;
        }
        this.restoreContext()
            .then(context => {
                let contextId = context.id;
                return this.testCaseReportsResource.query({
                    contextId,
                    reportType: this.selectedReportType.key,
                    iso: this.fromIso,
                    toIso: this.toIso,
                    topIsoCount: this.topIsoCount,
                    jobNames: this.jobNamesForFilter,
                    q: this.query
                }).$promise;
            })
            .then(response => {
                this.loading = false;
                this.configureChartData(response.data);
                this.populateIsoVersions(response.meta);
                this.activityLabels = this.reportsService.collectTestActivityLabels(response.meta);
                if (!this.activityLabelsEnabled) {
                    this.enabledActivityLabels = this.activityLabels;
                    this.activityLabelsEnabled = true;
                }
                this.computeColumns();
                this.computeRows();

                if (this.tableControlObject) {
                    this.tableControlObject.reload();
                }
            });

        this.$state.go('.', this.getState());
    }

    getState() {
        let resultCode = this.resultCode ? this.resultCode.join() : null;
        let state = {reportType: this.selectedReportType.key, resultCode};
        if (this.topIsoCount) {
            state.topIsoCount = this.topIsoCount;
            state.fromISO = null;
            state.toISO = null;
        } else {
            state.topIsoCount = null;
            state.fromISO = this.fromIso;
            state.toISO = this.toIso;
        }
        return state;
    }

    populateIsoVersions(meta) {
        this.iso = _.get(meta, 'isoVersions');

        if (!this.fromIso && !this.topIsoCount) {
            let index = this.iso.length > 4 ? 4 : this.iso.length;
            this.fromIso = this.iso[index];
        }
        if (!this.toIso) {
            this.toIso = this.iso[0];
        }
        this.syncIsoRangeSelection();
        this.syncTopIsoSelection();
    }

    configureChart() {
        this.chartOptions = {
            chart: {
                type: 'lineChart',
                height: 250,
                duration: 300,
                xAxis: {
                    rotateLabels: 0,
                    showMaxMin: false,
                    tickFormat: d => {
                        let value = this.chartData[0].values[d];
                        if (value) {
                            return value.xLabel;
                        }
                        return null;
                    },
                    axisLabel: 'ISO version'
                },
                yAxis: {
                    showMaxMin: false,
                    tickFormat: d => {
                        return `${d}%`;
                    },
                    axisLabel: 'Pass Rate'
                },
                yDomain: [0, 100]
            },
            title: {
                enable: false
            }
        };
        this.chartData = [];
    }

    configureChartData(data) {
        this.tableData = {};
        this.chartData = [];
        this.tableIso = [];
        let i = 0;
        let chartDataMap = {};

        this.dropIsoMap = {};

        _.forEach(data, datum => {
            let isoVersion = _.get(datum, 'isoVersion');
            let dropName = _.get(datum, 'dropName');

            if (_.isUndefined(this.dropIsoMap[dropName])) {
                this.dropIsoMap[dropName] = [];
            }
            this.dropIsoMap[dropName].push(isoVersion);
            this.tableIso.push(isoVersion);

            _.forEach(datum.data, groupData => {
                let type = _.get(groupData, 'groupBy');

                _.defaults(chartDataMap, {[type]: []});
                _.defaults(this.tableData, {[type]: {}});
                this.tableData[type][isoVersion] = groupData.passRate;

                chartDataMap[type].push({x: i, y: groupData.passRate, xLabel: isoVersion, dropName});
            });

            i++;
        });

        _.mapKeys(chartDataMap, (values, key) => {
            let color;

            switch (key) {
                case 'Blocker':
                    color = '#e32119';
                    break;
                case 'Minor':
                    color = '#89ba17';
                    break;
                case 'Normal':
                    color = '#00285f';
                    break;
                default:
            }
            this.chartData.push({key, values, color});
        });

        this.chartOptions.chart.height = 250 + _.size(this.tableData) * 10;
    }

    groupName() {
        return _.startCase(this.selectedReportType.key.substr(6));
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

    getColor(passRate) {
        if (passRate === 100) {
            return 'trend-green';
        } else if (passRate >= 75) {
            return 'trend-yellow';
        } else if (passRate >= 0) {
            return 'trend-red';
        } else {
            return 'trend-white';
        }
    }

    formatPercent(passRate) {
        if (passRate !== undefined) {
            return `${passRate}%`;
        } else {
            return '-';
        }
    }

    hideChart() {
        return _.size(this.tableData) > 10;
    }

    getSortedData() {
        let transformedData = _.map(this.tableData, (value, key) => {
            value.group = key;
            return value;
        });

        return _.sortBy(transformedData, 'group');
    }

    initResultCodeFilter(resultCode) {
        this.resultCode = resultCode ? resultCode.split(',') : null;
    }

    buildQuery() {
        let self = this;
        let opts = {q: ''};
        let filter = {
            filter() {
                return {
                    resultCode: {value: self.resultCode}
                };
            }
        };
        this.tableParamsFactory.applyFiltering(opts, filter);
        this.query = opts.q;
    }

    initIsoSelection($stateParams) {
        if ($stateParams.topIsoCount) {
            this.topIsoCount = $stateParams.topIsoCount;
            this.toIso = null;
            this.fromIso = null;
        } else {
            this.topIsoCount = null;
            this.toIso = $stateParams.toISO || null;
            this.fromIso = $stateParams.fromISO || null;
        }
    }

    onIsoRangeChanged() {
        this.syncTopIsoSelection();
        this.onFilterChanged();
    }

    syncTopIsoSelection() {
        if (!_.isEmpty(this.iso) && this.toIso === this.iso[0]) {
            let index = this.iso.indexOf(this.fromIso);
            this.topIsoCount = index > -1 ? index + 1 : null;
        } else {
            this.topIsoCount = null;
        }
    }

    onTopIsoChanged() {
        this.syncIsoRangeSelection();
        this.onFilterChanged();
    }

    syncIsoRangeSelection() {
        if (!_.isEmpty(this.iso) && this.topIsoCount) {
            if (this.topIsoCount > this.iso.length) {
                this.topIsoCount = this.iso.length;
            }
            this.fromIso = this.iso[Number(this.topIsoCount) - 1];
            this.toIso = this.iso[0];
        }
    }

    getTopNList(search) {
        let newList = this.topNList.slice();
        let count = Number(search);
        if (Number.isInteger(count) && count > 0 && newList.indexOf(count) === -1) {
            newList.unshift(count);
        }
        return newList;
    }

    getStatuses() {
        return this.statusService.getStatuses().sort();
    }

    getStorageKey() {
        if (this.context) {
            return `tvs.context.${this.context.id}.trend-report.grid.columns.${this.selectedReportType.key}` +
                `.from.${this.fromIso}.to.${this.toIso}`;
        } else {
            return undefined;
        }
    }

    getSelectedDropIsoMap(columns) {
        let reload = false;
        let isEmpty = _.isEmpty(this.selectedDropIsoVersions);
        _.forEach(this.dropIsoMap, (versions, drop) => {
            _.forEach(versions, iso => {
                let isoSelected = _.find(columns, (column) => {
                    return column.title() === iso && column.show();
                });
                if (isoSelected) {
                    if (this.selectedDropIsoVersions[drop] === undefined) {
                        this.selectedDropIsoVersions[drop] = [];
                        if (!isEmpty) {
                            reload = true;
                        }
                    }
                    if (this.selectedDropIsoVersions[drop].indexOf(iso) === -1) {
                        this.selectedDropIsoVersions[drop].push(iso);
                    }
                } else {
                    _.pull(this.selectedDropIsoVersions[drop], iso);
                }
            });
            if (this.selectedDropIsoVersions[drop] !== undefined && _.isEmpty(this.selectedDropIsoVersions[drop])) {
                reload = true;
            }
        });
        if (reload) {
            this.selectedDropIsoVersions = {};
            return this.getSelectedDropIsoMap(columns);
        } else {
            return this.selectedDropIsoVersions;
        }
    }
}

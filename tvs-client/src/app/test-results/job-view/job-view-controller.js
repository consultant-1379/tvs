export default class JobViewController {
    constructor($state,
                $stateParams,
                $filter,
                $q,
                $window,
                contextResource,
                jobsResource,
                testSessionsResource,
                testSessionsInsertResource,
                tmsGridService,
                moment,
                modelService,
                dateTimeFormat,
                errorReporter,
                tvsTableService,
                statusService,
                tceRolesService,
                jobTooltips) {
        'ngInject';

        this.jobsResource = jobsResource;
        this.contextResource = contextResource;
        this.testSessionsResource = testSessionsResource;
        this.testSessionsInsertResource = testSessionsInsertResource;
        this.tmsGridService = tmsGridService;
        this.modelService = modelService;
        this.tvsTableService = tvsTableService;
        this.statusService = statusService;
        this.tceRolesService = tceRolesService;
        this.jobTooltips = jobTooltips;

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.$filter = $filter;
        this.$q = $q;
        this.d3 = $window.d3;

        this.errorReporter = errorReporter;

        this.job = {id: this.$stateParams.jobId};

        this.moment = moment;
        this.dateTimeFormat = dateTimeFormat;
        this.portletFields = [];
        this.processing = {
            testSession: null
        };

        this.topNList = [50, 100, 200, 300, 500, 1000];
        this.topTestSessionCount = 100;

        this.xLabels = [{
            key: 'date',
            value: 'Date'
        }, {
            key: 'iso',
            value: 'ISO Version'
        }, {
            key: 'drop',
            value: 'Drop'
        }];
        this.selectedLabel = this.xLabels[0];

        this.labelFormatter = {
            date: (data) => this.moment(data.timestamp).format(this.dateTimeFormat.DATE),
            iso: (data) => data.iso,
            drop: (data) => data.drop
        };

        this.loadJob();
        this.configureColumns();
        this.configureChart();
        this.reloadChart();
        this.configurePortlet();

        this.portletJob = {};
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

    configureColumns() {
        this.predefinedColumns = {
            executionId: {
                position: 0,
                tooltip: this.jobTooltips.executionId
            },
            'time.startDate': {
                position: 1,
                tooltip: this.jobTooltips.startDate
            },
            'time.duration': {
                position: 2,
                tooltip: this.jobTooltips.duration
            },
            'testCaseCount': {
                position: 3,
                tooltip: this.jobTooltips.testCaseCount
            },
            'testSuiteCount': {
                position: 4,
                tooltip: this.jobTooltips.testSuiteCount
            },
            'passRate': {
                position: 5,
                tooltip: this.jobTooltips.passRate
            },
            'ISO_VERSION': {
                position: 6,
                tooltip: this.jobTooltips.isoVersion
            },
            'DROP_NAME': {
                position: 7,
                tooltip: this.jobTooltips.dropName
            },
            'logReferences': {
                disabled: true
            },
            'ignored': {
                show: false,
                filter: false,
                additional: {
                    onClick: this.toggleIgnored.bind(this),
                    isProcessing: this.isProcessing.bind(this)
                }
            },
            uri: {
                show: false,
                tooltip: this.jobTooltips.uri
            },
            resultCode: {
                show: false,
                tooltip: this.jobTooltips.resultCode
            },
            lastExecutionDate: {
                show: false,
                tooltip: this.jobTooltips.lastExecutionDate
            },
            'time.stopDate': {
                show: false,
                tooltip: this.jobTooltips.stopDate
            },
            'modifiedDate': {
                show: false,
                tooltip: this.jobTooltips.modifiedDate
            },
            'createdDate': {
                show: false,
                tooltip: this.jobTooltips.createdDate
            },
            'JENKINS_JOB_NAME': {
                show: false,
                tooltip: this.jobTooltips.jenkinsJobName
            },
            'ISO_ARTIFACT_ID': {
                show: false,
                tooltip: this.jobTooltips.isoArtifactId
            }
        };
    }

    toggleIgnored(testSession) {
        if (!this.isAuthorized()) {
            return;
        }

        let contextId = _.get(this.job, 'context');
        let jobName = _.get(this.job, 'name');
        let executionId = testSession.executionId;

        this.setProcessing(testSession);
        this.testSessionsInsertResource.create({
            contextId,
            jobName,
            executionId
        }, {
            ignored: !testSession.ignored
        }).$promise
            .then((response) => {
                let updatedTestSession = response.data;
                testSession.ignored = updatedTestSession.ignored;
            })
            .finally(() => {
                this.setProcessing(null);
            });
    }

    isAuthorized() {
        return this.tceRolesService.hasRole(['ROLE_TEST_ENGINEER', 'ROLE_TEST_MANAGER']);
    }

    setProcessing(testSession) {
        this.processing.testSession = testSession;
    }

    isProcessing(testSession) {
        return _.get(this.processing, 'testSession.id') === testSession.id;
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

    configureChart() {
        this.chartData = [
            {key: 'Passed', values: [], color: '#89ba17'},
            {key: 'Failed', values: [], color: '#e32119'},
            {key: 'Broken', values: [], color: '#f08a00'},
            {key: 'Cancelled', values: [], color: '#fabb00'}
        ];
        this.chartOptions = {
            chart: {
                type: 'stackedAreaChart',
                height: 250,
                margin: {
                    top: 20,
                    right: 50,
                    bottom: 30,
                    left: 50
                },
                useVoronoi: false,
                clipEdge: true,
                duration: 100,
                transitionDuration: 500,
                useInteractiveGuideline: true,
                xAxis: {
                    showMaxMin: true,
                    tickFormat: d => {
                        let value = this.chartData[0].values[d];
                        if (value) {
                            return this.labelFormatter[this.selectedLabel.key](value);
                        }
                        return null;
                    }
                },
                yAxis: {
                    tickFormat: d => {
                        return this.d3.format('d')(d);
                    }
                },
                interactiveLayer: {
                    tooltip: {
                        headerFormatter: d => {
                            let value = this.chartData[0].values[d];
                            if (value) {
                                let date = this.moment(value.timestamp)
                                    .format(`${this.dateTimeFormat.DATE} ${this.dateTimeFormat.TIME}`);
                                return `Drop: <span class="unbold">${value.drop}</span>
                            <br/>ISO: <span class="unbold">${value.iso}</span>
                            <br/><span class="unbold">${date}</span>`;
                            }
                            return null;
                        }
                    }
                },
                zoom: {
                    enabled: true,
                    translate: [0, 0],
                    useFixedDomain: false,
                    useNiceScale: false,
                    horizontalOff: false,
                    verticalOff: true,
                    unzoomEventType: 'dblclick.zoom'
                },
                showControls: false
            },
            title: {
                enable: true,
                text: 'Test Execution Trends',
                className: 'h4'
            }
        };
    }

    reloadChart(q) {
        this.jobsResource.getStatistics({
            id: _.get(this.job, 'id'),
            q,
            limit: this.topTestSessionCount
        }).$promise.then(res => this.normalizeChartData(res));
    }

    fetchData(page, size, orderBy, orderMode, q) {
        let jobId = _.get(this.job, 'id');
        this.reloadChart(q);
        return this.testSessionsResource.query({
            jobId, page, size, orderBy, orderMode, q
        }).$promise.then(res => {
            this.total = res.meta.totalItems;
            return res;
        });
    }

    navigateToTestSession(row) {
        if (!row.ignored) {
            this.$state.go('testSession', {
                jobId: this.job.id,
                executionId: row.id
            });
        }
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    getStorageKey() {
        return 'tvs.job.grid.columns';
    }

    normalizeChartData(res) {
        _.forEach(this.chartData, d => {
            d.values = [];
        });

        let i = 0;
        _.forEach(res.data, el => {
            _.forEach(this.chartData, resultObj => {
                let timestamp = this.moment(el.time).valueOf();
                let iso = el.isoVersion;
                let drop = el.dropName;
                let status = resultObj.key.toLowerCase();

                resultObj.values.push({
                    x: i,
                    y: el[status],
                    timestamp,
                    iso,
                    drop
                });
            });
            i++;
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

    getTopNList(search) {
        let newList = this.topNList.slice();
        let count = Number(search);
        if (Number.isInteger(count) && count > 0 && newList.indexOf(count) === -1) {
            newList.unshift(count);
        }
        return newList;
    }
}

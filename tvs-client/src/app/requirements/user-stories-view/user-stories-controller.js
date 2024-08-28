export default class EpicsViewController {
    constructor($state,
                $stateParams,
                tvsTableService,
                requirementsResource,
                modelService,
                endpointLinkFactory,
                userStoryTooltips) {
        'ngInject';

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.requirementsResource = requirementsResource;
        this.modelService = modelService;
        this.endpointLinkFactory = endpointLinkFactory;
        this.userStoryTooltips = userStoryTooltips;

        this.mrId = $stateParams.mrId;
        this.epicId = $stateParams.epicId;
        this.tvsTableService = tvsTableService;

        $stateParams.orderBy = $stateParams.orderBy || 'testCaseCount';
        $stateParams.orderMode = $stateParams.orderMode || 'DESC';

        this.configureColumns();
        this.configurePortlet();
        this.loadMainRequirement();
        this.loadEpic();
    }

    loadMainRequirement() {
        this.requirementsResource.get({id: this.mrId})
            .$promise
            .then(mainReq => {
                this.mrData = this.modelService.cleanCopy(mainReq.data);
                _.set(this.mrData, 'linkToIssue', this.endpointLinkFactory.namJIRARequirement(this.mrId));
            });
    }

    loadEpic() {
        this.requirementsResource.get({id: this.epicId})
            .$promise
            .then(epic => {
                this.epicData = this.modelService.cleanCopy(epic.data);
                _.set(this.epicData, 'linkToIssue', this.endpointLinkFactory.namJIRARequirement(this.epicId));
            });
    }

    configureColumns() {
        this.predefinedColumns = [
            {
                field: 'id',
                title: 'User Story ID',
                tooltip: this.userStoryTooltips.userStoryId,
                type: 'String',
                context: null
            }, {
                field: 'name',
                title: 'Name',
                tooltip: this.userStoryTooltips.name,
                type: 'String',
                context: null
            }, {
                field: 'testCaseCount',
                title: 'Test Case Count',
                tooltip: this.userStoryTooltips.testCaseCount,
                type: 'NumberShort',
                context: null
            }, {
                field: 'passedTestCaseCount',
                title: 'Passed Test Case Count',
                tooltip: this.userStoryTooltips.passedTestCaseCount,
                type: 'StatusSuccess',
                columnType: 'number',
                context: null
            }, {
                field: 'failedTestCaseCount',
                title: 'Failed Test Case Count',
                tooltip: this.userStoryTooltips.failedTestCaseCount,
                type: 'StatusBroken',
                columnType: 'number',
                context: null
            }, {
                field: 'passRate',
                title: 'Pass Rate',
                tooltip: this.userStoryTooltips.passRate,
                type: 'Rate',
                context: null
            }, {
                field: 'linkToIssue',
                title: 'Link To Issue',
                tooltip: this.userStoryTooltips.linkToIssue,
                type: 'Link',
                context: null,
                show: false,
                sortingDisabled: true,
                filter: false
            }
        ];
    }

    configurePortlet() {
        this.mrPortletFields = [{
            key: 'SOC',
            filter: x => x != null ? `${Number(x.toFixed(2))}%` : null
        }, {
            key: 'SOV',
            filter: x => x != null ? `${Number(x.toFixed(2))}%` : null
        }, {
            key: 'linkToIssue',
            filter: 'linky:\'_blank\''
        }, {
            key: 'usWithTestResults',
            name: 'USs With Test Results'
        }];

        this.epicPortletFields = [{
            key: 'SOC',
            filter: x => x != null ? `${Number(x.toFixed(2))}%` : null
        }, {
            key: 'SOV',
            filter: x => x != null ? `${Number(x.toFixed(2))}%` : null
        }, {
            key: 'linkToIssue',
            filter: 'linky:\'_blank\''
        }, {
            key: 'usWithTestResults',
            name: 'USs With Test Results'
        }];
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.requirementsResource.getChildren({id: this.epicId, page, size, orderBy, orderMode, q})
            .$promise
            .then(req => {
                _.forEach(req.data, r => {
                    _.set(r, 'linkToIssue', this.endpointLinkFactory.namJIRARequirement(r.id));
                });

                return req;
            });
    }

    navigate(row) {
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    getStorageKey() {
        return `tvs.us.${this.mrId}.${this.epicId}.grid.columns`;
    }

}

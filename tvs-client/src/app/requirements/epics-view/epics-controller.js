export default class MainRequirementsViewController {
    constructor($state,
                $stateParams,
                tvsTableService,
                requirementsResource,
                modelService,
                endpointLinkFactory,
                epicsTooltips) {
        'ngInject';

        this.$state = $state;
        this.$stateParams = $stateParams;
        this.requirementsResource = requirementsResource;
        this.modelService = modelService;
        this.endpointLinkFactory = endpointLinkFactory;
        this.epicsTooltips = epicsTooltips;

        this.mrId = $stateParams.mrId;
        this.tvsTableService = tvsTableService;

        $stateParams.orderBy = $stateParams.orderBy || 'testCaseCount';
        $stateParams.orderMode = $stateParams.orderMode || 'DESC';

        this.loadMainRequirement();
        this.configureColumns();
        this.configurePortlet();
    }

    loadMainRequirement() {
        this.requirementsResource.get({id: this.mrId})
            .$promise
            .then(mainReq => {
                this.mrData = this.modelService.cleanCopy(mainReq.data);
                _.set(this.mrData, 'linkToIssue', this.endpointLinkFactory.namJIRARequirement(this.mrId));
            });
    }

    configureColumns() {
        this.predefinedColumns = [
            {
                field: 'id',
                title: 'Requirement ID',
                tooltip: this.epicsTooltips.epicId,
                type: 'String',
                context: null
            }, {
                field: 'name',
                title: 'Name',
                tooltip: this.epicsTooltips.name,
                type: 'String',
                context: null
            }, {
                field: 'testCaseCount',
                title: 'Test Case Count',
                tooltip: this.epicsTooltips.testCaseCount,
                type: 'NumberShort',
                context: null
            }, {
                field: 'userStoryCount',
                title: 'User Story Count',
                tooltip: this.epicsTooltips.userStoryCount,
                type: 'NumberShort',
                context: null
            }, {
                field: 'usWithTestResults',
                title: 'User Stories with results',
                tooltip: this.epicsTooltips.usWithTestResults,
                type: 'umberShort',
                context: null
            }, {
                field: 'SOC',
                title: 'SOC',
                tooltip: this.epicsTooltips.SOC,
                type: 'Rate',
                context: null
            }, {
                field: 'SOV',
                title: 'SOV',
                tooltip: this.epicsTooltips.SOV,
                type: 'Rate',
                context: null
            }, {
                field: 'linkToIssue',
                title: 'Link To Issue',
                tooltip: this.epicsTooltips.linkToIssue,
                type: 'Link',
                context: null,
                show: false,
                sortingDisabled: true,
                filter: false
            }
        ];
    }

    configurePortlet() {
        this.portletFields = [
            {
                key: 'usWithTestResults',
                name: 'USs With Test Results'
            },
            {
                key: 'SOC',
                filter: x => x != null ? `${Number(x.toFixed(2))}%` : null
            }, {
                key: 'SOV',
                filter: x => x != null ? `${Number(x.toFixed(2))}%` : null
            }, {
                key: 'linkToIssue',
                filter: 'linky:\'_blank\''
            }];
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.requirementsResource.getChildren({id: this.mrId, page, size, orderBy, orderMode, q})
            .$promise
            .then(req => {
                _.forEach(req.data, r => {
                    _.set(r, 'linkToIssue', this.endpointLinkFactory.namJIRARequirement(r.id));
                });

                return req;
            });
    }

    navigate(row) {
        this.$state.go('userStories', {mrId: this.mrId, epicId: row.id});
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    getStorageKey() {
        return `tvs.epic.${this.mrId}.grid.columns`;
    }

}

export default class MainRequirementsViewController {
    constructor($state,
                $stateParams,
                tvsTableService,
                requirementsResource,
                endpointLinkFactory,
                requirementsTooltips) {
        'ngInject';

        this.$state = $state;
        this.tvsTableService = tvsTableService;
        this.endpointLinkFactory = endpointLinkFactory;

        this.requirementsResource = requirementsResource;
        this.requirementsTooltips = requirementsTooltips;

        $stateParams.orderBy = $stateParams.orderBy || 'testCaseCount';
        $stateParams.orderMode = $stateParams.orderMode || 'DESC';

        this.configureColumns();
    }

    configureColumns() {
        this.predefinedColumns = [
            {
                field: 'id',
                title: 'Requirement ID',
                tooltip: this.requirementsTooltips.requirementId,
                type: 'String',
                context: null
            }, {
                field: 'name',
                title: 'Name',
                tooltip: this.requirementsTooltips.name,
                type: 'String',
                context: null
            }, {
                field: 'epicCount',
                title: 'EPIC Count',
                tooltip: this.requirementsTooltips.epicCount,
                type: 'NumberShort',
                context: null
            }, {
                field: 'testCaseCount',
                title: 'Test Case Count',
                tooltip: this.requirementsTooltips.testCaseCount,
                type: 'NumberShort',
                context: null
            }, {
                field: 'userStoryCount',
                title: 'User Story Count',
                tooltip: this.requirementsTooltips.userStoryCount,
                type: 'NumberShort',
                context: null
            }, {
                field: 'usWithTestResults',
                title: 'User Stories with results',
                tooltip: this.requirementsTooltips.usWithTestResults,
                type: 'NumberShort',
                context: null
            }, {
                field: 'SOC',
                title: 'SOC',
                tooltip: this.requirementsTooltips.SOC,
                type: 'Rate',
                context: null
            }, {
                field: 'SOV',
                title: 'SOV',
                tooltip: this.requirementsTooltips.SOV,
                type: 'Rate',
                context: null
            }, {
                field: 'linkToIssue',
                title: 'Link To Issue',
                tooltip: this.requirementsTooltips.linkToIssue,
                type: 'Link',
                context: null,
                show: false,
                sortingDisabled: true,
                filter: false
            }
        ];
    }

    fetchData(page, size, orderBy, orderMode, q) {
        return this.requirementsResource.query({page, size, orderBy, orderMode, q})
            .$promise
            .then(req => {
                _.forEach(req.data, r => {
                    _.set(r, 'linkToIssue', this.endpointLinkFactory.namJIRARequirement(r.id));
                });

                return req;
            });
    }

    navigate(row) {
        this.$state.go('epics', {mrId: row.id});
    }

    computeColumns(data, meta) {
        return this.tvsTableService.generateColumns(this.predefinedColumns, meta.columns);
    }

    getStorageKey() {
        return `tvs.main-requirements.grid.columns`;
    }

}

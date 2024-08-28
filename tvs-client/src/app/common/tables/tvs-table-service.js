export default class TvsTableService {

    constructor(tmsGridService) {
        'ngInject';
        this.tmsGridService = tmsGridService;
    }

    generateColumns(predefinedColumnConfig, genericColumnConfig) {
        genericColumnConfig = _.indexBy(genericColumnConfig, (item) => item.field);
        let config = this.defaultsDeep({}, genericColumnConfig, predefinedColumnConfig);

        let enabledColumns =
            _.filter(config, column => !_.get(predefinedColumnConfig, [column.field, 'disabled']));

        return _.map(enabledColumns, enabledColumn => {

            let columnData = _.defaults({}, predefinedColumnConfig[enabledColumn.field], enabledColumn);
            let column = {
                title: columnData.title,
                tooltip: columnData.tooltip,
                field: columnData.field,
                formatFilter: this.getFilterByType(columnData.type),
                show: columnData.show == null ? true : columnData.show,
                type: this.getCellByType(columnData.type),
                width: this.getWidthByType(columnData.type),
                navigate: columnData.navigate,
                columnType: columnData.columnType || this.getColumnType(columnData.type),
                filter: columnData.filter,
                additional: columnData.additional,
                position: columnData.position
            };
            if (!columnData.sortingDisabled) {
                column.sortField = columnData.field;
            }

            return this.tmsGridService.createColumnObject(column);
        });
    }

    // TODO upgrade lodash to have _.defaultsDeep
    defaultsDeep(object, source1, source2) {
        if (source1) {
            object = this.defaults(object, source1);
        }
        if (source2) {
            object = this.defaults(object, source2);
        }

        return object;
    }

    defaults(object, source) {
        _.mapKeys(source, (value, key) => {
            if (object[key] === undefined) {
                object[key] = {};
            }
            _.defaults(object[key], source[key]);
        });

        return object;
    }

    getFilterByType(type) {
        switch (type) {
            case 'Rate':
                return x => x != null ? `${Number(x.toFixed(2))}%` : null;
            case 'DateTime':
                return 'tmsDateTime';
            case 'Duration':
                return 'tmsDuration';
            case 'StatusSuccess':
                return text => this.getColoredTextTemplate(text, 'text-success');
            case 'StatusCancelled':
                return text => this.getColoredTextTemplate(text, 'text-muted');
            case 'StatusBroken':
                return text => this.getColoredTextTemplate(text, 'text-danger');
            case 'StatusPending':
                return text => this.getColoredTextTemplate(text, 'text-warning');
            default:
                return null;
        }
    }

    getColumnType(type) {
        switch (type) {
            case 'Rate':
            case 'NumberShort':
            case 'NumberLong':
                return 'number';
            case 'DateTime':
                return 'date';
            case 'Duration':
                return 'duration';
            case 'Status':
                return 'result';
            case 'StatusSuccess':
            case 'StatusCancelled':
            case 'StatusBroken':
            case 'StatusPending':
            case 'Link':
            case 'String':
            default:
                return 'string';
        }
    }

    getCellByType(type) {
        switch (type) {
            case 'Boolean':
                return 'boolean';
            case 'Link':
                return 'link';
            case 'Status':
                return 'test-case-status';
            case 'StatusSuccess':
            case 'StatusCancelled':
            case 'StatusBroken':
            case 'StatusPending':
                return 'html';
            case 'InnerLink':
                return 'inner-link';
            case 'Select':
                return 'ui-select';
            default:
                return undefined;
        }
    }

    getWidthByType(type) {
        switch (type) {
            case 'DateTime':
            case 'NumberLong':
            case 'Duration':
                return '12em';
            case 'StatusSuccess':
            case 'StatusCancelled':
            case 'StatusBroken':
            case 'StatusPending':
            case 'Status':
            case 'Rate':
            case 'NumberShort':
            case 'StringShort':
                return '9em';
            default:
                return null;
        }
    }

    getColoredTextTemplate(text, textClass) {
        return `<span class="${textClass}">${text}</span>`;
    }

}


export default class TableFilterService {
    constructor(tableParamsFactory) {
        'ngInject';

        this.tableParamsFactory = tableParamsFactory;

        this.operationMap = {
            '>': (value, filterValue) => value > filterValue,
            '<': (value, filterValue) => value < filterValue,
            '=': (value, filterValue) => value === filterValue
        };

        this.transformerMap = {
            'string': this.handleString,
            'number': this.handleNumber
        };
    }

    filterRows(rows, query, mapper) {
        let filteredRows = _.clone(rows);
        let filters = this.tableParamsFactory.parseFilter(query);

        let self = this;
        _.forOwn(filters, function(filter, key) {
            filteredRows = _.filter(filteredRows, function(row) {
                let value = row[key];
                if (mapper) {
                    value = mapper(value, filter.type);
                }
                let result = self.transformerMap[filter.type](value, filter.value, filter.operation, self);
                return result;
            });
        });

        return filteredRows;
    }

    handleNumber(value, filterValue, operation, service) {
        return service.operationMap[operation](Number(value), Number(filterValue));
    }

    handleString(value, filterValue, operation) {
        if (operation === '~') {
            if (value && filterValue) {
                return value.toLowerCase().indexOf(filterValue.toLowerCase()) !== -1;
            } else {
                return true;
            }
        } else {
            throw new Error('Unsupported operation!');
        }
    }
}

export default class ObjectPortletController {
    constructor($scope, modelService) {
        'ngInject';
        this.modelService = modelService;

        let cols = this.colCount;
        if (!cols) {
            this.colCount = 1;
        }
        if (cols > 12) {
            this.colCount = 12;
        }

        $scope.$watch('vm.source', source => this.generate(source));
        $scope.$watch('vm.fieldConfig', fields => this.generate(this.object));

    }

    generate(object) {
        let fieldConfig = _.groupBy(this.fieldConfig, value => {
            return value.key;
        });

        this.object = this.flatten(object, {});

        let fields = _.map(this.object, (value, key) => {
            let defField = fieldConfig[key];
            return _.defaults({}, {key}, _.get(defField, '[0]'), {value});
        });

        let fieldCountPerColumn = Math.ceil(fields.length / this.colCount);

        this.fieldsByColumns = [];
        for (let i = 0; i < this.colCount; i++) {
            this.fieldsByColumns.push(
                fields.slice(i * fieldCountPerColumn, (i + 1) * fieldCountPerColumn)
            );
        }
    }

    flatten(x, result, prefix) {
        if (_.isObject(x)) {
            _.each(x, (v, k) => {
                this.flatten(v, result, prefix ? prefix + '.' + k : k);
            });
        } else {
            result[prefix] = x;
        }

        return result;
    }

    createArrayOfLength(number) {
        return new Array(number);
    }

    getColumnClass() {
        return 'col-md-' + Math.round(12 / this.colCount);
    }

    getFieldsForColumn(index) {
        return this.fieldsByColumns[index];
    }

    getTmsPropKeyClass(field) {
        return _.kebabCase('tms-prop-key-' + field);
    }

    getTmsPropValueClass(field) {
        return _.kebabCase('tms-prop-value-' + field);
    }
}

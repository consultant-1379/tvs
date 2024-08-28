const dashboardUsersMap = new WeakMap();
const dashboardUrlsMap = new WeakMap();

export default class DashboardController {
    constructor($state, $scope, dashboardUsers, dashboardUrls) {
        'ngInject';

        dashboardUsersMap.set(this, dashboardUsers);
        dashboardUrlsMap.set(this, dashboardUrls);

        let chartColor = ['#FABB00', '#00A9D4', '#E32119', '#F08A00', '#89BA17', '#B1B3B4', '#00625F', '#0066B3',
            '#5C5C5C', '#7B0663', '#FF7600'];

        $scope.options = {
            chart: {
                type: 'discreteBarChart',
                height: 450,
                margin: {
                    top: 20,
                    right: 20,
                    bottom: 50,
                    left: 55
                },
                x(d) {
                    return d.label;
                },
                y(d) {
                    return d.value;
                },
                showValues: true,
                staggerLabels: true,
                color: chartColor,
                valueFormat(d) {
                    return d3.format(',.0f')(d);
                },
                duration: 500,
                yAxis: {
                    tickFormat: d3.format(',.0f')
                }
            }
        };

        $scope.options2 = {
            chart: {
                type: 'discreteBarChart',
                height: 450,
                margin: {
                    top: 20,
                    right: 20,
                    bottom: 50,
                    left: 55
                },
                x(d) {
                    return d.label;
                },
                y(d) {
                    return d.value;
                },
                showValues: true,
                showXAxis: false,
                color: chartColor,
                valueFormat(d) {
                    return d3.format(',.0f')(d);
                },
                duration: 500,
                yAxis: {
                    tickFormat: d3.format(',.0f')
                }
            }
        };

        dashboardUsersMap.get(this).$promise.then((items) => {
            let data = this.convertData('Users', items.concat());
            $scope.userData = data;
        });

        dashboardUrlsMap.get(this).$promise.then((items) => {
            let data = this.convertData('Url Count', items.concat());
            $scope.urlData = data;
        });
    }

    convertData(name, data) {
        let dataSet = [];
        let obj = {
            key: name,
            values: data
        };

        dataSet.push(obj);
        return dataSet;
    }
}

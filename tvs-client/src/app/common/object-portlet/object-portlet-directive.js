export default function objectPortletDirective() {
    return {
        restrict: 'E',
        scope: {
            tip: '@',
            title: '@',
            source: '=',
            fieldConfig: '=',
            colCount: '='
        },
        templateUrl: 'app/common/object-portlet/object-portlet.html',
        bindToController: true,
        controller: 'ObjectPortletController',
        controllerAs: 'vm'
    };
}

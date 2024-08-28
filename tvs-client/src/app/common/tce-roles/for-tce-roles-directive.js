export default function forTceRolesDirective(ngIfDirective,
                                             userSessionService,
                                             tceRolesService) {
    'ngInject';

    let ngIf = ngIfDirective[0];

    return {
        restrict: 'A',
        transclude: 'element',
        link($scope, $element, $attrs) {
            let roles = userSessionService.parseRoles($attrs.forTceRoles);
            $attrs.ngIf = () => tceRolesService.hasRole(roles);
            ngIf.link.apply(ngIf, arguments);
        }
    };
}


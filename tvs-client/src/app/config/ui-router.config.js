export default function($stateProvider, $urlRouterProvider) {
    'ngInject';

    $stateProvider
        .state('default', {
            parent: 'login'
        })
        .state('about', {
            url: '/about',
            templateUrl: 'app/about/about.html',
            ncyBreadcrumb: {
                label: 'Home'
            }
        })
        .state('login', {
            url: '/login?to',
            templateUrl: 'app/login/login.html',
            controller: 'LoginController'
        })
        .state('administration', {
            abstract: true,
            url: '/administration',
            template: '<ui-view/>',
            resolve: {
                $title() {
                    return 'Administration';
                },
                auth(tceRolesService) {
                    return tceRolesService.resolve(['ROLE_CONTEXT_ADMIN']);
                }
            }
        })
        .state('administration.jobs', {
            parent: 'administration',
            url: '/jobs?page&q&orderMode&orderBy',
            template: '<job-administration/>',
            reloadOnSearch: false,
            ncyBreadcrumb: {
                label: 'Job Administration'
            }
        })
        .state('notifications', {
            parent: 'resultsParent',
            url: '/jobs/{jobId}/notifications',
            template: '<notifications/>',
            reloadOnSearch: false,
            ncyBreadcrumb: {
                label: 'Notifications',
                parent: 'job'
            },
            resolve: {
                auth(tceRolesService) {
                    return tceRolesService.resolve(['ROLE_TEST_ENGINEER']);
                }
            }
        })
        .state('resultsParent', {
            abstract: true,
            template: '<ui-view/>',
            resolve: {
                $title() {
                    return 'Results';
                },
                auth(authResolver) {
                    return authResolver.resolve([]);
                }
            }
        })
        .state('jobs', {
            parent: 'resultsParent',
            url: '/jobs?page&q&orderMode&orderBy',
            templateUrl: 'app/test-results/jobs.html',
            reloadOnSearch: false,
            ncyBreadcrumb: {
                label: 'Test Results'
            }
        })
        .state('job', {
            parent: 'resultsParent',
            url: '/jobs/{jobId}?page&q&orderMode&orderBy',
            templateUrl: 'app/test-results/job.html',
            reloadOnSearch: false,
            ncyBreadcrumb: {
                label: 'Test Activity {{job.name}}',
                parent: 'jobs'
            }
        })
        .state('testFlakiness', {
            parent: 'resultsParent',
            url: '/jobs/{jobId}/flakiness',
            templateUrl: 'app/test-results/test-flakiness.html',
            ncyBreadcrumb: {
                label: 'Test Flakiness',
                parent: 'job'
            }
        })
        .state('flakinessExecutionHistory', {
            parent: 'resultsParent',
            url: '/jobs/{jobId}/flakiness/{testCaseId}/history?startTime&endTime',
            reloadOnSearch: false,
            templateUrl: 'app/test-results/flakiness-execution-history.html',
            ncyBreadcrumb: {
                label: 'Test Case Flakiness & Execution History',
                parent: 'testFlakiness'
            }
        })
        .state('testSession', {
            parent: 'resultsParent',
            url: '/jobs/{jobId}/test-sessions/{executionId}?page&q&orderMode&orderBy',
            reloadOnSearch: false,
            templateUrl: 'app/test-results/test-session.html',
            ncyBreadcrumb: {
                label: 'Test Session {{testSession.name}}',
                parent: 'job'
            }
        })
        .state('testSuiteResult', {
            parent: 'resultsParent',
            url: '/jobs/{jobId}/test-sessions/{executionId}/test-suite-results/' +
            '{testSuiteName:string}?page&q&orderMode&orderBy',
            reloadOnSearch: false,
            templateUrl: 'app/test-results/test-suite-result.html',
            ncyBreadcrumb: {
                label: 'Test Suite Result {{testSuiteResult.id}}',
                parent: 'testSession'
            }
        })
        .state('executionHistory', {
            parent: 'resultsParent',
            url: '/test-case-results/{testCaseId}/history?page&q&orderMode&orderBy',
            reloadOnSearch: false,
            templateUrl: 'app/test-results/test-case-execution-history.html',
            ncyBreadcrumb: {
                label: 'Test Case Execution History',
                parent: 'jobs'
            }
        })
        .state('dropReport', {
            parent: 'resultsParent',
            url: '/jobs/{jobId}/drop-report?page&q&orderMode&orderBy?reportType',
            reloadOnSearch: false,
            templateUrl: 'app/reports/drop-report.html',
            ncyBreadcrumb: {
                label: 'Drop Report',
                parent: 'job'
            }
        }).state('isoReport', {
            parent: 'resultsParent',
            url: '/iso-report?orderMode&orderBy&ISO&reportType',
            reloadOnSearch: false,
            templateUrl: 'app/reports/iso-report.html',
            ncyBreadcrumb: {
                label: 'Test Run Report',
                parent: 'jobs'
            }
        })
        .state('trendReport', {
            parent: 'resultsParent',
            url: '/trend-report?fromISO&toISO&topIsoCount&reportType&resultCode&q',
            reloadOnSearch: false,
            templateUrl: 'app/reports/trend-report.html',
            ncyBreadcrumb: {
                label: 'Trend Report',
                parent: 'jobs'
            }
        })
        .state('mainRequirements', {
            parent: 'resultsParent',
            url: '/main-requirements?page&q&orderBy&orderMode',
            reloadOnSearch: false,
            templateUrl: 'app/requirements/main-requirements.html',
            ncyBreadcrumb: {
                label: 'Main Requirements',
                parent: 'jobs'
            }
        })
        .state('epics', {
            parent: 'resultsParent',
            url: '/main-requirements/{mrId}/epics?page&q&orderBy&orderMode',
            reloadOnSearch: false,
            templateUrl: 'app/requirements/epics.html',
            ncyBreadcrumb: {
                label: 'Epics',
                parent: 'mainRequirements'
            }
        })
        .state('userStories', {
            parent: 'resultsParent',
            url: '/main-requirements/{mrId}/epics/{epicId}/user-stories?page&q&orderBy&orderMode',
            reloadOnSearch: false,
            templateUrl: 'app/requirements/user-stories.html',
            ncyBreadcrumb: {
                label: 'User Stories',
                parent: 'epics'
            }
        })
        .state('dashboard', {
            url: '/dashboard',
            templateUrl: 'app/dashboard/dashboard.html',
            controller: 'dashboardController as vm',
            ncyBreadcrumb: {
                label: 'Dashboard'
            },
            resolve: {
                dashboardUsers(dashboardResource) {
                    return dashboardResource.get();
                },
                dashboardUrls(dashboardResource) {
                    return dashboardResource.getDashboardUrls();
                }
            }
        });

    $urlRouterProvider.otherwise('login');
}

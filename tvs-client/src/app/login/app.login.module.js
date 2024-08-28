import loginFormDirective from './login-form/login-form-directive';
import LoginController from './login-controller';
import LoginFormController from './login-form/login-form-controller';

const moduleName = 'app.login';

angular.module(moduleName, [])
    .directive('loginForm', loginFormDirective)
    .controller('LoginController', LoginController)
    .controller('LoginFormController', LoginFormController);

export default moduleName;

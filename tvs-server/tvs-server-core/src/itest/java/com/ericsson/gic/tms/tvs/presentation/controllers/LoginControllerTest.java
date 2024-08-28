package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.validation.ServiceException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.UserBean;
import com.ericsson.gic.tms.tvs.presentation.dto.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static com.ericsson.gic.tms.presentation.validation.CommonServiceError.BAD_CREDENTIALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class LoginControllerTest extends AbstractIntegrationTest {

    @Autowired
    private LoginController controller;

    @Before
    public void setUp() {
        controller.setHttpServletRequest(mock(HttpServletRequest.class));

        MockHttpServletRequest req = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));
    }

    @After
    public void logOut() {
        controller.logout();
    }

    @Test
    public void testNullChecks() {
        assertThatThrownBy(() -> controller.login(null))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void defaultUserIsAnonymous() {
        assertThat(controller.status().getName())
            .isEqualTo("anonymousUser");
    }

    @Test
    public void loginWithBadCredentialsThrows() {
        assertThatThrownBy(() -> controller.login(getBadUserCredentials()))
            .isInstanceOf(ServiceException.class)
            .hasMessage(BAD_CREDENTIALS.getMessage());
    }

    @Test
    public void testLogin() {
        UserCredentials credentials = getMockUserCredentials();

        UserBean loginUserInfo = controller.login(credentials);
        UserBean statusUserInfo = controller.status();

        assertThat(loginUserInfo).isEqualToIgnoringGivenFields(statusUserInfo, "authorities");
        assertThat(loginUserInfo.isAuthenticated()).isTrue();
    }

    @Test
    public void testCapsLogin() {
        UserCredentials credentials = new UserCredentials();
        credentials.setLogin("USER");
        credentials.setPassword("password");

        UserBean loginInfo = controller.login(credentials);

        assertThat(loginInfo.isAuthenticated()).isTrue();

        UserBean logout = controller.logout();
        assertThat(logout.isAuthenticated()).isFalse();

        credentials = new UserCredentials();
        credentials.setLogin("user");
        credentials.setPassword("password");

        loginInfo = controller.login(credentials);

        assertThat(loginInfo.isAuthenticated()).isTrue();
    }

    @Test
    public void testExternalUserLogin() {
        UserCredentials credentials = new UserCredentials();
        credentials.setLogin("consultant");
        credentials.setPassword("password");

        UserBean loginInfo = controller.login(credentials);

        assertThat(loginInfo.isAuthenticated()).isTrue();
    }

    @Test
    public void testLogout() {
        controller.login(getMockUserCredentials());
        UserBean logoutUserInfo = controller.logout();

        assertThat(logoutUserInfo.isAuthenticated()).isFalse();

        UserBean status = controller.status();
        assertThat(status.isAuthenticated()).isFalse();
    }

    private UserCredentials getBadUserCredentials() {
        UserCredentials credentials = new UserCredentials();
        credentials.setLogin("BAD");
        credentials.setPassword("BAD");
        return credentials;
    }

    private UserCredentials getMockUserCredentials() {
        UserCredentials credentials = new UserCredentials();
        credentials.setLogin("user");
        credentials.setPassword("password");
        return credentials;
    }

}

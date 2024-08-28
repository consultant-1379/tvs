package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.tvs.application.security.AuthenticationService;
import com.ericsson.gic.tms.tvs.presentation.dto.UserBean;
import com.ericsson.gic.tms.tvs.presentation.dto.UserCredentials;
import com.ericsson.gic.tms.tvs.presentation.resources.LoginResource;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

@Controller
public class LoginController implements LoginResource {

    private HttpServletRequest req;

    @Autowired
    private AuthenticationService authenticationService;

    @Context
    @VisibleForTesting
    void setHttpServletRequest(HttpServletRequest req) {
        this.req = req;
    }

    @Override
    public UserBean status() {
        return authenticationService.getCurrentUser();
    }

    @Override
    public UserBean login(UserCredentials userCredentials) {
        req.getSession(true);
        return authenticationService.login(userCredentials);
    }

    @Override
    public UserBean logout() {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return authenticationService.logout();
    }

}

package com.ericsson.gic.tms.tvs.application.security;

import com.ericsson.gic.tms.presentation.validation.ServiceException;
import com.ericsson.gic.tms.tvs.application.services.UserSessionService;
import com.ericsson.gic.tms.tvs.presentation.dto.UserBean;
import com.ericsson.gic.tms.tvs.presentation.dto.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

import static com.ericsson.gic.tms.presentation.validation.CommonServiceError.*;
import static java.util.Collections.*;

@Service
public class AuthenticationService {

    protected static final Authentication ANONYMOUS_USER = new AnonymousAuthenticationToken("key", "anonymousUser",
        singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserSessionService userSessionService;

    public UserBean login(UserCredentials credentials) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            credentials.getLogin(),
            credentials.getPassword());

        Authentication authentication = authenticate(token);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        String name = securityContext.getAuthentication().getName();

        userSessionService.addUserSession(sessionId, name);

        return getUserBean(authentication);
    }

    public UserBean logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();

        return getUserBean(getAuthentication());
    }

    public UserBean getCurrentUser() {
        return getUserBean(getAuthentication());
    }

    private UserBean getUserBean(Authentication auth) {
        UserBean userBean = new UserBean();
        userBean.setAuthenticated(!isAnonymous(auth));
        userBean.setName(auth.getName());

        return userBean;
    }

    private boolean isAnonymous(Authentication auth) {
        return !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken;
    }

    private Authentication getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .orElse(ANONYMOUS_USER);
    }

    private Authentication authenticate(Authentication authentication) {
        try {
            return authenticationManager.authenticate(authentication);
        } catch (AuthenticationException ex) {
            throw new ServiceException(BAD_CREDENTIALS);
        }
    }

}

package com.ericsson.gic.tms.tvs.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.ericsson.gic.tms.infrastructure.Profiles.FULL;
import static com.ericsson.gic.tms.infrastructure.Profiles.PRODUCTION;
import static com.ericsson.gic.tms.infrastructure.Profiles.STAGE;

@Configuration
@EnableWebSecurity
@Profile({STAGE, PRODUCTION, FULL})
public class ExternalSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${ldap.url}")
    private String url;

    @Value("${ldap.search.base}")
    private String searchBase;

    @Value("${ldap.dnPattern}")
    private String dnPattern;

    @Value("${ldap.dnPatternExternal}")
    private String dnPatternExternal;

    @Value("${ldap.user}")
    private String user;

    @Value("${ldap.password}")
    private String password;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .anyRequest().permitAll().and()
            .headers().cacheControl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
            .userDnPatterns(dnPattern, dnPatternExternal)
            .userSearchBase(searchBase)
            .contextSource()
            .managerDn(user)
            .managerPassword(password)
            .url(url);
    }

    @Override
    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}

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

import static com.ericsson.gic.tms.infrastructure.Profiles.DEVELOPMENT;
import static com.ericsson.gic.tms.infrastructure.Profiles.INTEGRATION_TEST;
import static com.ericsson.gic.tms.infrastructure.Profiles.TEST;

@Configuration
@EnableWebSecurity
@Profile({DEVELOPMENT, TEST, INTEGRATION_TEST})
public class EmbeddedSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${ldap.dnPattern}")
    private String dnPattern;

    @Value("${ldap.dnPatternExternal}")
    private String dnPatternExternal;

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
        auth
            .ldapAuthentication()
            .userDnPatterns(dnPattern, dnPatternExternal)
            .contextSource().ldif("classpath:users.ldif");
    }

    @Override
    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}

package com.ericsson.gic.tms.tvs.infrastructure;

import com.ericsson.gic.tms.tvs.application.services.UrlCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class CounterConfiguration implements Filter {

    @Autowired
    private UrlCountService urlCountService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // needed as override method but not used
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest &&
                "GET".equalsIgnoreCase(((HttpServletRequest) servletRequest).getMethod())) {
            String url = ((HttpServletRequest) servletRequest).getPathInfo();

            Matcher matcher = Pattern.compile("([^/]+)").matcher(url);
            if (matcher.find()) {
                urlCountService.increment(matcher.group(1));
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // not used
    }
}

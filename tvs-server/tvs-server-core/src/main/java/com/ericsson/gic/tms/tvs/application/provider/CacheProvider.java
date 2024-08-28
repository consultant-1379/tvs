package com.ericsson.gic.tms.tvs.application.provider;

import com.ericsson.gic.tms.tvs.application.services.ResultCodeService;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ResultCode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map.Entry;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         15/09/2016
 */
@Configuration
public class CacheProvider {

    @Bean
    public LoadingCache<Entry<String, String>, ResultCode> provideResultCodeCache(
        ResultCodeService loader) {
        return CacheBuilder.newBuilder()
            .build(loader);
    }
}

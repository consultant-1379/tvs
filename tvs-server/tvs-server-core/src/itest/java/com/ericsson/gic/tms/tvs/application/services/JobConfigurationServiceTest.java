package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.JobConfiguration;
import com.ericsson.gic.tms.tvs.domain.repositories.JobConfigurationRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class JobConfigurationServiceTest extends AbstractIntegrationTest {

    private static final String NAM = "NAM";
    private static final String KGB_REGEX = "KGB_[0-9]+";
    private static final String RFA_REGEX = "RFA_.*";
    private static final String CONTEXT_ID = "contextId";
    private static final String CONTEXT_ID_2 = "contextId2";

    @Autowired
    private JobConfigurationService jobConfigurationService;

    @Autowired
    private JobConfigurationRepository jobConfigurationRepository;

    @Test
    public void singleConfigration() {
        jobConfigurationRepository.save(getJobConfiguration(CONTEXT_ID, KGB_REGEX, NAM));

        assertThat(jobConfigurationService.getContext("KGB_123", "NAM").getContextId())
            .isEqualTo(CONTEXT_ID);
    }

    @Test
    public void multipleConfigurations() {
        jobConfigurationRepository.save(getJobConfiguration(CONTEXT_ID, KGB_REGEX, NAM));
        jobConfigurationRepository.save(getJobConfiguration(CONTEXT_ID_2, RFA_REGEX, NAM));

        assertThat(jobConfigurationService.getContext("KGB_123", "NAM").getContextId())
            .isEqualTo(CONTEXT_ID);
        assertThat(jobConfigurationService.getContext("RFA_shortLoop", "NAM").getContextId())
            .isEqualTo(CONTEXT_ID_2);
    }

    @Test
    public void absentConfiguration() {
        assertThat(jobConfigurationService.getContext("KGB_123", "NAM").getContextId())
            .isNull();
    }

    private JobConfiguration getJobConfiguration(String context, String job, String source) {
        JobConfiguration jobConfiguration = new JobConfiguration();
        jobConfiguration.setContextId(context);
        jobConfiguration.setJobName(job);
        jobConfiguration.setSource(source);

        return jobConfiguration;
    }

}

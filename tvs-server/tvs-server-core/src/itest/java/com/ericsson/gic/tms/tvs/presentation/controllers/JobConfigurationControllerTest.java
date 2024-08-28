package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.dto.JobConfigurationBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class JobConfigurationControllerTest extends AbstractIntegrationTest {

    private static final String SOURCE = "ENM";

    private static final String RFA_JOB_NAME = "RFA_Job";
    private static final String ERICPOL_JOB_NAME_1 = "ERICPol_Security_Tribe_244_KGB_TAFtests";
    private static final String ERICPOL_JOB_NAME_2 = "Nemesis(Ericpol)_SecurityTribe_245_KGB_TAFtests";
    private static final String HYBRID_JOB_NAME_WITHOUT_TRIBE_CONTEXT = "RFA_123_KGB_shortLoop";
    private static final String HYBRID_JOB_NAME_WITH_TRIBE_CONTEXT = "AP_Tribe_123_KGB_RFA_Job";
    private static final String RANDOM_JOB_NAME = "dummyJob";

    private static final String CONTEXT_MAINTRACK = "55d4b2ac-e9e1-11e5-9ce9-5e5517507c66";
    private static final String CONTEXT_ERICPOL = "55d4bf7c-e9e1-11e5-9ce9-5e5517507c66";
    private static final String CONTEXT_AP = "55d4b4d2-e9e1-11e5-9ce9-5e5517507c66";
    private static final String CONTEXT_OTHER = "55d4d1e8-e9e1-11e5-9ce9-5e5517507c66";
    private static final String CONTEXT_OTHER_RFA = "55d4d2e8-e9e1-11e5-9ce9-5e5517507c66";

    @Autowired
    private JobConfigurationController controller;

    @Before
    public void setUp() {
        controller.setJsonApiUtil(mockedJsonApiUtil);
    }

    @Test
    public void maintrackJob() {
        JobConfigurationBean config = controller.get(RFA_JOB_NAME, SOURCE).unwrap();

        assertThat(config.getContextId()).isEqualTo(CONTEXT_MAINTRACK);
    }

    @Test
    public void kgbJobs() {
        JobConfigurationBean config1 = controller.get(ERICPOL_JOB_NAME_1, SOURCE).unwrap();
        JobConfigurationBean config2 = controller.get(ERICPOL_JOB_NAME_2, SOURCE).unwrap();

        assertThat(config1.getContextId()).isEqualTo(CONTEXT_ERICPOL);
        assertThat(config2.getContextId()).isEqualTo(CONTEXT_ERICPOL);
    }

    @Test
    public void kgbPriorityIsHigherThanOtherRfa() {
        JobConfigurationBean config = controller.get(HYBRID_JOB_NAME_WITH_TRIBE_CONTEXT, SOURCE).unwrap();

        assertThat(config.getContextId()).isEqualTo(CONTEXT_AP);
    }

    @Test
    public void otherRfaWinsWhenNoContextForTribe() {
        JobConfigurationBean config = controller.get(HYBRID_JOB_NAME_WITHOUT_TRIBE_CONTEXT, SOURCE).unwrap();

        assertThat(config.getContextId()).isEqualTo(CONTEXT_OTHER_RFA);
    }

    @Test
    public void randomJobsGoToOther() {
        JobConfigurationBean config = controller.get(RANDOM_JOB_NAME, SOURCE).unwrap();

        assertThat(config.getContextId()).isEqualTo(CONTEXT_OTHER);
    }

}

package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.JobConfiguration;
import com.ericsson.gic.tms.tvs.domain.repositories.JobConfigurationRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.JobConfigurationBean;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JobConfigurationService {

    @Autowired
    private JobConfigurationRepository jobConfigurationRepository;

    @Autowired
    private MapperFacade mapperFacade;

    public JobConfigurationBean getContext(String jobName, String source) {
        List<JobConfiguration> configurations = jobConfigurationRepository.findBySource(source);

        Optional<JobConfiguration> configuration = configurations.stream()
            .filter(jobConfiguration -> {
                Matcher matcher = Pattern.compile(jobConfiguration.getJobName()).matcher(jobName);
                return matcher.matches();
            })
            .findFirst();

        if (configuration.isPresent()) {
            return mapperFacade.map(configuration.get(), JobConfigurationBean.class);
        } else {
            return new JobConfigurationBean();
        }
    }
}

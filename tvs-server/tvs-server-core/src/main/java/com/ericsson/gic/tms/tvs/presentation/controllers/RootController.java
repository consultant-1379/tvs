package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.duraci.datawrappers.ResultCode;
import com.ericsson.gic.tms.infrastructure.mapping.mappers.dto.TimeWindowStatisticsMapper;
import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.References;
import com.ericsson.gic.tms.presentation.dto.bean.StatisticsBean;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Document;
import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.application.services.AggregationTriggerService;
import com.ericsson.gic.tms.tvs.application.services.AsyncAggregationTriggerService;
import com.ericsson.gic.tms.tvs.presentation.dto.AggregationInfoBean;
import com.ericsson.gic.tms.tvs.presentation.dto.StatusListBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus;
import com.ericsson.gic.tms.tvs.presentation.resources.RootResource;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.EnvironmentEndpoint;
import org.springframework.boot.actuate.endpoint.mvc.HealthMvcEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Controller
public class RootController extends AbstractJsonApiCapableController
    implements RootResource {

    @Autowired
    private References references;

    @Autowired(required = false)
    private HealthMvcEndpoint healthMvcEndpoint;

    @Autowired(required = false)
    private EnvironmentEndpoint envEndpoint;

    @Inject
    private Provider<MonitoringStatistics> statistics;

    @Autowired
    private TimeWindowStatisticsMapper timeWindowStatisticsMapper;

    @Autowired
    private AsyncAggregationTriggerService asyncAggregationTriggerService;

    @Autowired
    private AggregationTriggerService aggregationTriggerService;

    @Value("${application.includeEiffelStatuses ?: false}")
    private Boolean includeEiffelStatuses;

    @Override
    public Document<References> getReferences() {
        return responseFor(references)
            .withSelfRel(RootResource.class, REFERENCES_PATH)
            .build();
    }

    @Override
    public String getHealth() {
        Health health = (Health) healthMvcEndpoint.invoke(null);
        return health.getStatus().getCode();
    }

    @Override
    public Map<String, Object> getEnvironment() {
        return envEndpoint.invoke();
    }

    @Override
    public StatisticsBean getStatistics() {
        if (statistics.get() == null) {
            throw new NotFoundException();
        }
        MonitoringStatistics snapshot = statistics.get().snapshot();
        TimeWindowStatistics stats = snapshot.getRequestStatistics().getTimeWindowStatistics().get(0L);
        return timeWindowStatisticsMapper.toDto(stats);
    }

    @Override
    public StatusListBean getStatuses() {
        StatusListBean statusListBean = new StatusListBean();
        Set<String> allureStatuses = Stream.of(TestExecutionStatus.values())
            .map(TestExecutionStatus::name)
            .collect(toSet());

        if (includeEiffelStatuses) {
            Set<String> eiffelStatuses = Stream.of(ResultCode.allRecognized)
                .map(ResultCode::toString)
                .collect(toSet());

            allureStatuses.addAll(eiffelStatuses);
        }

        statusListBean.setStatuses(allureStatuses);
        return statusListBean;
    }

    @Override
    public AggregationInfoBean getAggregationInfo() {
        return getAggregationInfoBean();
    }

    @Override
    public AggregationInfoBean aggregate() {
        asyncAggregationTriggerService.trigger();

        return getAggregationInfoBean();
    }

    private AggregationInfoBean getAggregationInfoBean() {
        AggregationInfoBean info = new AggregationInfoBean();
        info.setLastRun(aggregationTriggerService.getLastRun());
        info.setRunning(aggregationTriggerService.isRunning());

        return info;
    }
}

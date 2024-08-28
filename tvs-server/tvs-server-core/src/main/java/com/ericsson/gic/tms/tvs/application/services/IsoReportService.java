package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.IsoComponentReport;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.IsoGroupReport;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.IsoPriorityReport;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoComponentReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoGroupReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoPriorityReportBean;
import ma.glasnost.orika.MapperFacade;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.TEST_CASE_RESULT;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.google.common.collect.Lists.newArrayList;

@Service
public class IsoReportService {

    @Autowired
    private Jongo jongo;

    @Autowired
    private MongoQueryService mongoQueryService;

    @Autowired
    private MapperFacade mapperFacade;

    public List<IsoPriorityReportBean> aggregateIsoPriorityReport(
            String iso, List<String> jobIds, Collection<String> contexts) {
        ArrayList<IsoPriorityReport> data = newArrayList(jongo.getCollection(TEST_CASE_RESULT.getName())
            .aggregate(mongoQueryService.getQuery(ISO_REPORT_MATCH), iso, jobIds, contexts, PRIORITY)
            .and(mongoQueryService.getQuery(ISO_REPORT_PROJECT))
            .and(mongoQueryService.getQuery(ISO_REPORT_GROUP_PRIORITY))
            .and(mongoQueryService.getQuery(ISO_REPORT_SORT_PRIORITY))
            .and(mongoQueryService.getQuery(ISO_REPORT_PROJECT_PASS_RATE))
            .as(IsoPriorityReport.class).iterator());

        return mapperFacade.mapAsList(data, IsoPriorityReportBean.class);
    }

    public List<IsoGroupReportBean> aggregateIsoGroupReport(
            String iso, List<String> jobIds, Collection<String> contexts) {
        ArrayList<IsoGroupReport> data = newArrayList(jongo.getCollection(TEST_CASE_RESULT.getName())
            .aggregate(mongoQueryService.getQuery(ISO_REPORT_MATCH), iso, jobIds, contexts, GROUPS)
            .and(mongoQueryService.getQuery(ISO_REPORT_UNWIND_GROUPS))
            .and(mongoQueryService.getQuery(ISO_REPORT_PROJECT))
            .and(mongoQueryService.getQuery(ISO_REPORT_GROUP_GROUP))
            .and(mongoQueryService.getQuery(ISO_REPORT_SORT_GROUP))
            .and(mongoQueryService.getQuery(ISO_REPORT_PROJECT_PASS_RATE))
            .as(IsoGroupReport.class).iterator());

        return mapperFacade.mapAsList(data, IsoGroupReportBean.class);
    }

    public List<IsoComponentReportBean> aggregateIsoComponentReport(
            String iso, List<String> jobIds, Collection<String> contexts) {
        ArrayList<IsoComponentReport> data = newArrayList(jongo.getCollection(TEST_CASE_RESULT.getName())
            .aggregate(mongoQueryService.getQuery(ISO_REPORT_MATCH), iso, jobIds, contexts, COMPONENTS)
            .and(mongoQueryService.getQuery(ISO_REPORT_UNWIND_COMPONENTS))
            .and(mongoQueryService.getQuery(ISO_REPORT_PROJECT))
            .and(mongoQueryService.getQuery(ISO_REPORT_GROUP_COMPONENT))
            .and(mongoQueryService.getQuery(ISO_REPORT_SORT_COMPONENT))
            .and(mongoQueryService.getQuery(ISO_REPORT_PROJECT_PASS_RATE))
            .as(IsoComponentReport.class).iterator());

        return mapperFacade.mapAsList(data, IsoComponentReportBean.class);
    }
}

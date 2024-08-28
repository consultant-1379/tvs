package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.presentation.dto.ContextBean;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.resources.ContextResource;
import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.application.services.IsoReportService;
import com.ericsson.gic.tms.tvs.application.services.IsoVersionService;
import com.ericsson.gic.tms.tvs.application.services.JobService;
import com.ericsson.gic.tms.tvs.application.services.QueryService;
import com.ericsson.gic.tms.tvs.application.services.TestCaseIsoTrendReportService;
import com.ericsson.gic.tms.tvs.application.services.TestSessionService;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.TestCaseResultTrendReport;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestCaseIsoTrendReportCsvMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.JobReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.GenericReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoCsvReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.ReportType;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.TestCaseIsoTrendBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.TestCaseIsoTrendReport;
import com.ericsson.gic.tms.tvs.presentation.resources.TestCaseReportResource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ericsson.gic.tms.presentation.dto.jsonapi.Meta.ISO_VERSIONS;
import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.reports.ReportType.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.getFirst;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;

@Controller
public class TestCaseReportController extends AbstractJsonApiCapableController implements TestCaseReportResource {

    private static final long TOP_ISO_COUNT = 5;
    private static final EnumSet TREND_REPORTS;
    private static final EnumSet ISO_VERSION_REPORTS;
    private static final String ISO_CSV_FILE_NAME = "inline; filename=\"%s-ISO-%s.csv\"";
    private static final String TREND_CSV_FILE_NAME = "inline; filename=\"%s-ISO-report.csv\"";
    private static final String JOB_DETAILS = "job-details";

    @Autowired
    private IsoReportService isoReportService;

    @Autowired
    private TestCaseIsoTrendReportService testCaseIsoTrendReportService;

    @Autowired
    private IsoVersionService isoVersionService;

    @Autowired
    private TestSessionService testSessionService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private ContextResource contextResource;

    @Autowired
    private JobService jobService;

    @Autowired
    private TestCaseIsoTrendReportCsvMapper trendReportMapper;

    @Autowired
    private MapperFacade mapperFacade;

    static {
        TREND_REPORTS = EnumSet.of(TREND_COMPONENT, TREND_GROUP, TREND_PRIORITY, TREND_REQUIREMENT, TREND_SUITE);
        ISO_VERSION_REPORTS = EnumSet.of(ISO_COMPONENT, ISO_GROUP, ISO_PRIORITY);
    }

    private static final Map<ReportType, String> REPORT_TYPE_TO_FIELD_NAME = ImmutableMap.<ReportType, String>builder()
        .put(TREND_COMPONENT, COMPONENTS)
        .put(TREND_GROUP, GROUPS)
        .put(TREND_PRIORITY, PRIORITY)
        .put(TREND_REQUIREMENT, REQUIREMENTS)
        .put(TREND_SUITE, SUITE_NAME)
        .build();

    @Override
    public DocumentList<?> getReport(String contextId, ReportType reportType, String isoVersion, String toIsoVersion,
                                     int topIsoCount, String jobNames, String query) {

        List<String> contextIds = getContextIds(contextId);
        List<String> allIsoVersions = testSessionService.getAllIsoVersions(contextIds);
        List<String> allJobNames = getAllJobNames(contextId);
        List<String> jobIdsList = getJobIds(jobNames, allJobNames);

        List<? extends GenericReportBean> report;

        if (TREND_REPORTS.contains(reportType)) {
            List<String> isoVersionsRange = findTrendIsoVersions(contextIds, isoVersion, toIsoVersion, topIsoCount);
            List<TestCaseResultTrendReport> trendReport =
                getTrendReport(reportType, isoVersionsRange, query, contextIds, jobIdsList);
            report = mapperFacade.mapAsList(trendReport, TestCaseIsoTrendBean.class);
        } else if (ISO_VERSION_REPORTS.contains(reportType)) {
            String iso = resolveIsoVersion(isoVersion, allIsoVersions);
            report = getIsoReport(reportType, iso, contextIds, jobIdsList);
        } else {
            throw new NotFoundException();
        }

        return responseFor(report)
            .withSelfRel(TestCaseReportResource.class)
            .withMeta(ISO_VERSIONS, allIsoVersions)
            .withMeta(JOB_DETAILS, allJobNames)
            .build();
    }

    private String resolveIsoVersion(String isoVersion, List<String> allIsoVersions) {
        if (isNullOrEmpty(isoVersion)) {
            return getFirst(allIsoVersions, null);
        }
        return isoVersion;
    }

    private List<String> getJobIds(String jobNames, List<String> allJobNames) {
        String names;
        if (isNullOrEmpty(jobNames)) {
            names = String.join(",", allJobNames);
        } else {
            names = jobNames;
        }
        return jobService.findJobIdsFromNames(names);
    }

    private List<String> getAllJobNames(String contextId) {
        Set<JobReport> allJobs = Sets.newHashSet();
        allJobs.addAll(jobService.findJobsByContextId(contextId));
        return allJobs.stream()
            .map(JobReport::getName)
            .collect(Collectors.toList());
    }

    private List<String> getContextIds(String contextId) {
        return contextResource.getChildren(contextId).unwrap().stream()
            .map(ContextBean::getId)
            .collect(toList());
    }

    private List<TestCaseResultTrendReport> getTrendReport(ReportType reportType,
                                                           List<String> isoVersionsRange,
                                                           String query,
                                                           List<String> contextIds,
                                                           List<String> jobIdsList) {
        if (!TREND_REPORTS.contains(reportType)) {
            throw new NotFoundException();
        }

        List<TestCaseResultTrendReport> report;
        String tag = REPORT_TYPE_TO_FIELD_NAME.get(reportType);
        Query parsedQuery = queryService.createQuery(query);
        report = testCaseIsoTrendReportService.aggregateByTag(contextIds, jobIdsList, isoVersionsRange, tag,
            parsedQuery);
        return report;
    }

    private List<? extends IsoReportBean<?>> getIsoReport(ReportType reportType,
                                                          String isoVersion,
                                                          List<String> contextIds,
                                                          List<String> jobIdsList) {
        List<? extends IsoReportBean<?>> report;
        switch (reportType) {
            case ISO_PRIORITY:
                report = isoReportService.aggregateIsoPriorityReport(isoVersion, jobIdsList, contextIds);
                break;
            case ISO_GROUP:
                report = isoReportService.aggregateIsoGroupReport(isoVersion, jobIdsList, contextIds);
                break;
            case ISO_COMPONENT:
                report = isoReportService.aggregateIsoComponentReport(isoVersion, jobIdsList, contextIds);
                break;
            default:
                throw new NotFoundException();
        }
        return report;
    }

    @Override
    public DocumentList<IsoCsvReport> exportIsoReports(String contextId, String jobNames, ReportType reportType,
                                                       String isoVersion) {
        List<String> contextIds = getContextIds(contextId);
        List<String> allIsoVersions = testSessionService.getAllIsoVersions(contextIds);
        List<String> jobIdsList = getJobIds(jobNames, getAllJobNames(contextId));
        String iso = resolveIsoVersion(isoVersion, allIsoVersions);

        List<? extends IsoReportBean<?>> report = getIsoReport(reportType, iso, contextIds, jobIdsList);
        List<IsoCsvReport> csvReport = mapperFacade.mapAsList(report, IsoCsvReport.class);

        return responseFor(csvReport)
            .withSelfRel(TestCaseReportResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(ISO_CSV_FILE_NAME, reportType.getName(), isoVersion))
            .build();
    }

    @Override
    public DocumentList<TestCaseIsoTrendReport> exportTrendReports(String contextId, String jobNames,
                                                                   ReportType reportType, String iso, String toIso) {
        List<String> contextIds = getContextIds(contextId);
        List<String> jobIdsList = getJobIds(jobNames, getAllJobNames(contextId));
        List<String> isoVersionsRange = findTrendIsoVersions(contextIds, iso, toIso, 0);

        List<TestCaseResultTrendReport> report =
            getTrendReport(reportType, isoVersionsRange, null, contextIds, jobIdsList);

        List<TestCaseIsoTrendReport> result = trendReportMapper.mapList(report);

        return responseFor(result)
            .withSelfRel(TestCaseReportResource.class)
            .withMeta(CONTENT_DISPOSITION, String.format(TREND_CSV_FILE_NAME, reportType.getName()))
            .build();
    }

    private List<String> findTrendIsoVersions(List<String> contextIds, String fromIsoVersion, String toIsoVersion,
                                              int topIsoCount) {

        if (isNullOrEmpty(fromIsoVersion) && isNullOrEmpty(toIsoVersion)) {
            long limit;
            if (topIsoCount > 0) {
                limit = topIsoCount;
            } else {
                limit = TOP_ISO_COUNT;
            }
            return isoVersionService.findLastIsoVersionsTopN(contextIds, limit);
        }
        return isoVersionService.findIsoVersionsInContexts(contextIds, fromIsoVersion, toIsoVersion);
    }
}

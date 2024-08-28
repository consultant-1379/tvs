package com.ericsson.gic.tms.tvs.infrastructure.mapping.dto;

import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.TestCaseResultTrendReport;
import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.record.GroupedPassRateRecord;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.TestCaseIsoTrendReport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestCaseIsoTrendReportCsvMapper {

    public List<TestCaseIsoTrendReport> mapList(List<TestCaseResultTrendReport> fromList) {

        return fromList.stream()
            .flatMap(report -> report.getData().stream()
                .map(data -> map(report, data)))
            .collect(Collectors.toList());
    }

    private static TestCaseIsoTrendReport map(TestCaseResultTrendReport fromReport, GroupedPassRateRecord fromRecord) {
        TestCaseIsoTrendReport toRecord = new TestCaseIsoTrendReport();

        toRecord.setId(fromRecord.getGroupBy());
        toRecord.setDropName(fromReport.getDropName());
        toRecord.setIsoVersion(fromReport.getIsoVersion());
        toRecord.setPassRate(fromRecord.getPassRate());

        return toRecord;
    }
}

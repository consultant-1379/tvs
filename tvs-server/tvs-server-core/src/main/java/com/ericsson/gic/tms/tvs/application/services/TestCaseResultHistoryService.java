package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ImportStatus;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.domain.repositories.TestCaseResultRepository;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestCaseMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestCaseReportMapper;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.TestCaseResultMapper;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseImportStatus;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseReport;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultHistoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class TestCaseResultHistoryService {

    @Autowired
    private TestCaseResultRepository testCaseResultRepository;

    @Autowired
    private TestCaseResultMapper testCaseResultMapper;

    @Autowired
    private TestCaseReportMapper testCaseReportMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    public Page<TestCaseResultBean> getTestCaseResults(TestCaseImportStatus importStatus, Pageable pageRequest) {
        Page<TestCaseResult> page;

        if (importStatus == null) {
            page = testCaseResultRepository.findAll(pageRequest);
        } else {
            ImportStatus status = ImportStatus.valueOf(importStatus.name());
            page = testCaseResultRepository.findByImportStatus(status, pageRequest);
        }
        return testCaseMapper.mapAsPage(page, pageRequest);
    }

    public Page<TestCaseResultHistoryBean> getTestCaseResultHistory(String testCaseId,
                                                                    LocalDateTime startTime,
                                                                    LocalDateTime stopTime,
                                                                    Pageable pageRequest,
                                                                    Query query) {
        Page<TestCaseResult> page;
        Date startDate = null;
        Date endDate = null;
        if (startTime != null  && stopTime != null) {
            startDate = DateUtils.toDate(startTime);
            endDate = DateUtils.toDate(stopTime);
        }
        page = testCaseResultRepository.findByName(testCaseId, startDate, endDate, pageRequest, query);
        return testCaseResultMapper.mapAsPage(page, pageRequest);
    }

    public List<TestCaseReport> getTestCase(String testCaseId) {
        List<TestCaseResult> testCaseResults = testCaseResultRepository.findByName(testCaseId);
        return testCaseReportMapper.mapAsList(testCaseResults);
    }
}

package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.common.DateUtils;
import com.ericsson.gic.tms.presentation.converters.DateConverter;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.application.services.TestSessionService;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ExecutionTime;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static org.assertj.core.api.Assertions.*;

public class TestSessionRepositoryTest extends AbstractIntegrationTest {

    public static final String TEST_SYSTEM_ID_001 = "test_system_id_001";

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private TestSessionService testSessionService;

    private DateConverter dateConverter = new DateConverter(ZoneId.of("UTC"));

    @Test
    public void save() {
        TestSession testSession = testSession(TEST_SYSTEM_ID_001);

        TestSession saved = testSessionRepository.save(testSession);
        TestSession found = testSessionRepository.findOne(saved.getId());

        assertThat(found.getExecutionId()).isEqualTo(testSession.getExecutionId());
    }

    @Test
    public void delete() {
        TestSession testSession = testSession(TEST_SYSTEM_ID_001);

        TestSession saved = testSessionRepository.save(testSession);
        TestSession found = testSessionRepository.findOne(saved.getId());
        testSessionRepository.delete(found.getId());
        TestSession deleted = testSessionRepository.findOne(saved.getId());

        assertThat(deleted).isNull();
    }

    @Test
    public void testFindByJobIdBetweenDates() {
        String jobId = uniqueString();
        TestSession testSession = testSession(jobId, uniqueString(),
            executionTime(date("2015-12-01T00:00:00"), date("2015-12-02T00:00:00")));
        TestSession testSession2 = testSession(jobId, uniqueString(),
            executionTime(date("2015-12-02T00:00:00"), date("2015-12-03T12:00:00")));

        LocalDateTime startDate = LocalDateTime.parse("2015-12-01T23:00:00");
        LocalDateTime stopDate = LocalDateTime.parse("2015-12-04T00:00:00");

        testSessionRepository.save(newArrayList(testSession, testSession2));

        Page<TestSessionBean> testSessions =
            testSessionService.getPaginatedTestSessions(jobId, startDate, stopDate, pageRequest(1, 20), null);

        assertThat(testSessions.getTotalElements())
            .isEqualTo(1);

        assertThat(testSessions.getContent().get(0).getId())
            .isEqualTo(testSession2.getExecutionId());
    }

    @Test
    public void testFindByExecutionId() {
        TestSession testSession = testSession(TEST_SYSTEM_ID_001);
        testSessionRepository.save(testSession);

        TestSession saved = testSessionRepository.findByExecutionId(TEST_SYSTEM_ID_001);

        assertThat(saved)
            .isEqualToIgnoringGivenFields(testSession, "time");
    }

    @Test
    public void testFindByJobId() {
        TestSession testSession = testSession(TEST_SYSTEM_ID_001);
        TestSession saved = testSessionRepository.save(testSession);

        List<TestSession> found = testSessionRepository.findByJobId(saved.getJobId());

        assertThat(found)
            .hasSize(1);

        assertThat(found.get(0))
            .isEqualToIgnoringGivenFields(testSession, "time");
    }

    @Test
    public void testFindByExecutionIdAndJobId() {
        TestSession testSession = testSession(TEST_SYSTEM_ID_001);
        TestSession saved = testSessionRepository.save(testSession);

        TestSession found =
            testSessionRepository.findByExecutionIdAndJobId(TEST_SYSTEM_ID_001, saved.getJobId());

        assertThat(found)
            .isEqualToIgnoringGivenFields(testSession, "time");
    }

    private PageRequest pageRequest(int page, int pageSize) {
        return new PageRequest(page - 1, pageSize);
    }

    private TestSession testSession(String executionId) {
        return testSession(UUID.randomUUID().toString(),
            executionId,
            executionTime(new Date(), new Date()));
    }

    private TestSession testSession(String jobId, String executionId, ExecutionTime time) {
        TestSession testSession = new TestSession();
        testSession.setJobId(jobId);
        testSession.setExecutionId(executionId);
        testSession.setTime(time);

        Map<String, Object> additionalFields = newHashMap();
        additionalFields.put(DROP_NAME, "16.2");
        additionalFields.put(ISO_ARTIFACT_ID, "ERICenm_CXP903938");
        additionalFields.put(ISO_VERSION, "1.18.41");
        additionalFields.put(JENKINS_JOB_NAME, "RFA_JOB");
        testSession.setAdditionalFields(additionalFields);
        return testSession;
    }

    private ExecutionTime executionTime(Date startDate, Date stopDate) {
        return new ExecutionTime(startDate, stopDate);
    }

    private String uniqueString() {
        return UUID.randomUUID().toString();
    }

    private Date date(String dateTime) {
        LocalDateTime parsedDate = LocalDateTime.parse(dateTime);
        return DateUtils.toDate(parsedDate);
    }
}

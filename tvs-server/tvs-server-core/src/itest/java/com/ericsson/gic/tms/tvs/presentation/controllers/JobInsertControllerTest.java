package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.dto.ContextBean;
import com.ericsson.gic.tms.presentation.dto.common.SortingMode;
import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Meta;
import com.ericsson.gic.tms.presentation.dto.jsonapi.Resource;
import com.ericsson.gic.tms.presentation.resources.ContextResource;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.FieldMetadata;
import com.ericsson.gic.tms.tvs.presentation.dto.ExecutionTimeBean;
import com.ericsson.gic.tms.tvs.presentation.dto.JobBean;
import com.ericsson.gic.tms.tvs.presentation.dto.TestSessionBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ericsson.gic.tms.presentation.dto.common.SortingMode.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JobInsertControllerTest extends AbstractIntegrationTest {

    private static final String CONTEXT_ID = "systemId-3";
    private static final String NEW_CONTEXT_ID = "newContextId";
    private static final String NEW_JOB_NAME = "newJobName";

    private static final String ID = "id";

    @Autowired
    private JobController jobController;

    @Autowired
    private JobInsertController jobInsertController;

    @Autowired
    private TestSessionController testSessionController;

    @Autowired
    private TestSuiteResultController testSuiteController;

    @Autowired
    private ContextResource contextResource;

    private JobBean jobBean;

    @Before
    public void setUp() {
        jobController.setJsonApiUtil(mockedJsonApiUtil);
        jobInsertController.setJsonApiUtil(mockedJsonApiUtil);
        testSessionController.setJsonApiUtil(mockedJsonApiUtil);
        testSuiteController.setJsonApiUtil(mockedJsonApiUtil);

        jobBean = new JobBean();
        jobBean.setTestSessions(emptyList());

        ContextBean contextBean = new ContextBean();
        contextBean.setId(CONTEXT_ID);
        doReturn(new DocumentList<>(newArrayList(new Resource<>(contextBean))))
            .when(contextResource).getChildren(CONTEXT_ID);
    }

    @Test
    public void createNewJob() {
        JobBean inserted = jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, jobBean).unwrap();

        JobBean job = jobController.getJob(inserted.getId()).unwrap();

        assertThat(job).isNotNull();
        assertThat(job.getId()).isEqualTo(inserted.getId());
        assertThat(job.getName()).isEqualTo(NEW_JOB_NAME);
        assertThat(job.getContext()).isEqualTo(NEW_CONTEXT_ID);
    }

    @Test
    public void additionalFieldsArePersisted() {
        jobBean.addAdditionalFields("extra", "something");
        JobBean job = jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, jobBean).unwrap();
        assertThat(job).isNotNull();
        assertThat(job.getAdditionalFields().get("extra")).isEqualTo("something");
    }

    @Test
    public void addAndUpdateExistingAdditionalFields() {
        jobBean.addAdditionalFields("extra", "something");
        JobBean job = jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, jobBean).unwrap();
        assertThat(job).isNotNull();
        assertThat(job.getAdditionalFields().get("extra")).isEqualTo("something");

        Map<String, Object> newFields = newHashMap();
        newFields.put("extra", "newThing");
        newFields.put("bonus", "newValue");

        job.resetImmutableFields();
        job.addAdditionalFields(newFields);
        job = jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, job).unwrap();

        assertThat(job).isNotNull();
        assertThat(job.getAdditionalFields().get("extra")).isEqualTo("newThing");
        assertThat(job.getAdditionalFields().get("bonus")).isEqualTo("newValue");
    }

    @Test
    public void metadataIsPopulated() {
        String generatedKey = UUID.randomUUID().toString();
        jobBean.addAdditionalFields(generatedKey, "something");

        jobInsertController.updateJob(CONTEXT_ID, NEW_JOB_NAME, jobBean);

        DocumentList<JobBean> jobDocument =
            jobController.getList(CONTEXT_ID, 1, 20, ID, SortingMode.ASC, "");

        assertThat(jobDocument).isNotNull();

        assertThat(jobDocument.getMeta()).isNotNull();
        Collection<FieldMetadata> columns = (Collection<FieldMetadata>) jobDocument.getMeta().get(Meta.COLUMNS);
        assertThat(columns).isNotNull();
        assertThat(columns).extracting(FieldMetadata::getField).contains(generatedKey);
    }

    @Test
    public void jobModifiedDate() {
        String jobId = jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, jobBean).unwrap().getId();
        JobBean inserted = jobController.getJob(jobId).unwrap();

        assertThat(inserted.getModifiedDate()).isNotNull();

        jobId = jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, jobBean).unwrap().getId();
        JobBean updated = jobController.getJob(jobId).unwrap();

        assertThat(updated.getId()).isEqualTo(inserted.getId());
        assertThat(updated.getModifiedDate()).isAfter(inserted.getModifiedDate());
    }

    @Test
    public void sessionsAreAppended() {
        JobBean inserted = jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, jobBean).unwrap();

        List<TestSessionBean> sessionBeans = testSessionController.getList(inserted.getId(), null, null,
            1, 20, ID, DESC, null).unwrap();

        assertThat(sessionBeans).isEmpty();

        List<TestSessionBean> testSessions = newArrayList();
        testSessions.add(prepareSessionBeans());

        jobBean.setTestSessions(testSessions);

        jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, jobBean).unwrap();

        sessionBeans = testSessionController.getList(inserted.getId(), null, null,
            1, 20, ID, DESC, null).unwrap();
        assertThat(sessionBeans).hasSize(1);
    }

    @Test
    public void createJobWithEmptyTestSessions() {
        jobBean.setTestSessions(null);  // must be not null
        assertThatThrownBy(() -> jobInsertController.updateJob(NEW_CONTEXT_ID, NEW_JOB_NAME, jobBean))
            .isInstanceOf(ConstraintViolationException.class);
    }

    private TestSessionBean prepareSessionBeans() {
        TestSessionBean sessionBean = new TestSessionBean();
        sessionBean.setId("unique_session_execution_id");
        sessionBean.setUri("losethegame.lv");
        sessionBean.setTestSuites(emptyList());

        ExecutionTimeBean timeBean = new ExecutionTimeBean();
        timeBean.setStartDate(new Date());
        sessionBean.setTime(timeBean);
        return sessionBean;
    }

}

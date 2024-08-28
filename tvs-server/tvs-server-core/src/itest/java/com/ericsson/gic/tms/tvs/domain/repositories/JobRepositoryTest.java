package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.util.ContainsFilter;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import org.jongo.Jongo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Map;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JobRepositoryTest extends AbstractIntegrationTest {

    public static final String X_SCHEDULER_2 = "X_SCHEDULER_2";

    public static final String CONTEXT_1 = "systemId-1";
    public static final String CONTEXT_2 = "systemId-2";

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private Jongo jongo;

    @Test
    public void findByContextId() {
        Page<Job> jobs = jobRepository.findByContextIdIn(singletonList(CONTEXT_1), mock(Pageable.class),
            new Query());

        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findByIdAndContextId() {
        Job expected = new Job();
        expected.setName(X_SCHEDULER_2);
        expected.setContextId(CONTEXT_2);

        Job job = jobRepository.findByNameAndContextId(X_SCHEDULER_2, CONTEXT_2);

        assertThat(job)
            .isNotNull()
            .isEqualToComparingOnlyGivenFields(expected, "name", "contextId");
    }

    @Test
    public void testUuidGeneration() {
        Job newJob = new Job();
        newJob.setName("random name");
        newJob.setContextId("random context");

        Job saved = jobRepository.save(newJob);

        assertThat(saved.getUid()).isNotEmpty();

        Job byUid = jobRepository.findOne(saved.getUid());

        assertThat(byUid).isNotNull();
    }

    @Test
    public void testModifiedDateGeneration() {
        Date dateBeforeSave = new Date();
        Job initial = jobRepository.findByNameAndContextId(X_SCHEDULER_2, CONTEXT_2);
        jobRepository.save(initial);

        Job saved = jobRepository.findByNameAndContextId(X_SCHEDULER_2, CONTEXT_2);

        assertThat(saved.getModifiedDate())
            .isNotNull();
            //.isAfter(dateBeforeSave); TODO: change Date to LocalDateTime and remove this line
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAdditionalFieldsSerialization() {
        Job job = new Job();
        job.addAdditionalFields("test1", 1);
        job.addAdditionalFields("test2", "string");
        job.addAdditionalFields("test3", 1L);

        Job save = jobRepository.save(job);

        Map<String, Object> result = jongo.getCollection(JOB.getName())
            .findOne("{uid: #}", save.getUid())
            .as(Map.class);

        assertThat(result)
            .isNotNull()
            .containsKeys("test1", "test2", "test3");
    }

    @Test
    public void testCaseInsensitiveSearch() {
        Job job = new Job();
        job.setName("UPPER_CASE_NAME");

        Job job2 = new Job();
        job2.setName("lower_case_name");

        jobRepository.save(newArrayList(job, job2));

        Query query = new Query()
            .addFilter(new ContainsFilter("name", "case"));
        Iterable<Job> jobs = jobRepository.findBy(query);

        assertThat(jobs)
            .hasSize(2)
            .extracting(Job::getName)
            .containsExactly("UPPER_CASE_NAME", "lower_case_name");
    }

}

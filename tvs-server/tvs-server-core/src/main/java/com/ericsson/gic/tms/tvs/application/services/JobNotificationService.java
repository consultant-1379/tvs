package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.presentation.validation.ServiceException;
import com.ericsson.gic.tms.tvs.domain.model.verdict.Job;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import com.ericsson.gic.tms.tvs.domain.model.verdict.notification.Criteria;
import com.ericsson.gic.tms.tvs.domain.model.verdict.notification.JobNotification;
import com.ericsson.gic.tms.tvs.domain.model.verdict.notification.Recipient;
import com.ericsson.gic.tms.tvs.domain.repositories.JobNotificationRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.JobRepository;
import com.ericsson.gic.tms.tvs.domain.repositories.TestSessionRepository;
import com.ericsson.gic.tms.tvs.domain.util.Filter;
import com.ericsson.gic.tms.tvs.domain.util.Query;
import com.ericsson.gic.tms.tvs.infrastructure.mail.EmailService;
import com.ericsson.gic.tms.tvs.infrastructure.mapping.dto.CriteriaMapper;
import com.ericsson.gic.tms.tvs.infrastructure.templating.TemplatingService;
import com.ericsson.gic.tms.tvs.presentation.dto.RecipientBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RuleBean;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.ericsson.gic.tms.presentation.validation.NotFoundException.verifyFound;
import static com.ericsson.gic.tms.tvs.infrastructure.templating.TemplatingService.TEST_SESSION_NOTIFICATION;
import static com.ericsson.gic.tms.tvs.infrastructure.templating.TemplatingService.TEST_SESSION_NOTIFICATION_SUBJECT;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class JobNotificationService {

    @Autowired
    private JobNotificationRepository notificationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private QueryService queryService;

    @Autowired
    private TemplatingService templatingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CriteriaMapper criteriaMapper;

    @Autowired
    private MapperFacade mapperFacade;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobNotificationService.class);


    public List<RuleBean> getRulesByJobId(String jobId) {
        return criteriaMapper.mapAsList(findOrCreateJobNotification(jobId).getRules());
    }

    public RuleBean updateRule(String jobId, String ruleId, RuleBean bean) {

        JobNotification notification = findOrCreateJobNotification(jobId);
        List<Criteria> rules = notification.getRules();

        Optional<Criteria> existingRule = getRule(rules, ruleId);

        Criteria rule;
        String id;
        if (existingRule.isPresent()) {
            rule = existingRule.get();
            id = ruleId;
        } else {
            rule = new Criteria();
            id = UUID.randomUUID().toString();
            rules.add(rule);
        }
        criteriaMapper.copyFields(bean, rule);
        rule.setId(id);

        JobNotification savedNotification = notificationRepository.save(notification);
        Criteria savedRule = getRule(savedNotification.getRules(), id).get();

        return criteriaMapper.toDto(savedRule);
    }

    public RuleBean removeRule(String jobId, String ruleId) {

        JobNotification notification = findOrCreateJobNotification(jobId);
        List<Criteria> rules = notification.getRules();

        Criteria entity = getRule(rules, ruleId).orElseThrow(() -> new NotFoundException());
        rules.removeIf(rule -> rule.getId().equals(ruleId));

        notificationRepository.save(notification);

        return criteriaMapper.toDto(entity);
    }

    private Optional<Criteria> getRule(List<Criteria> rules, String ruleId) {
        return rules.stream()
            .filter((rule) -> rule.getId().equals(ruleId))
            .findFirst();
    }

    public List<RecipientBean> getRecipientsByJobId(String jobId) {

        JobNotification notification = findOrCreateJobNotification(jobId);
        return mapperFacade.mapAsList(notification.getRecipients(), RecipientBean.class);
    }

    public RecipientBean addRecipient(String jobId, RecipientBean recipientBean) {

        JobNotification notification = findOrCreateJobNotification(jobId);
        List<Recipient> recipients = notification.getRecipients();

        Optional<Recipient> existing = getRecipient(recipients, recipientBean.getEmail());

        Recipient recipient;
        if (existing.isPresent()) {
            recipient = existing.get();
        } else {
            recipient = mapperFacade.map(recipientBean, Recipient.class);
            recipients.add(recipient);
            notificationRepository.save(notification);
        }

        return mapperFacade.map(recipient, RecipientBean.class);
    }

    public RecipientBean removeRecipient(String jobId, String email) {

        JobNotification notification = findOrCreateJobNotification(jobId);
        List<Recipient> recipients = notification.getRecipients();

        Recipient removed = getRecipient(recipients, email).orElseThrow(() -> new NotFoundException());
        recipients.removeIf(recipient -> recipient.getEmail().equals(email));

        notificationRepository.save(notification);

        return mapperFacade.map(removed, RecipientBean.class);
    }

    private Optional<Recipient> getRecipient(List<Recipient> recipients, String email) {
        return recipients.stream()
            .filter((recipient) -> recipient.getEmail().equals(email))
            .findFirst();
    }

    public void verifyJobExecution(String jobId, String executionId) {

        JobNotification notification = findOrCreateJobNotification(jobId);
        List<Criteria> rules = notification.getRules();

        List<Criteria> triggeredRules = Lists.newArrayList();
        TestSession triggeredTestSession = null;

        for (Criteria rule : rules) {
            Collection<Filter> filters = queryService.ruleToFilters(rule);
            Query query = new Query(filters);
            TestSession testSession = testSessionRepository.findByExecutionId(executionId, query);

            if (testSession != null) {
                triggeredTestSession = testSession;
                triggeredRules.add(rule);

                Object actual = getField(rule, testSession);
                rule.setLastTriggeredValue(actual);
                rule.setLastTriggeredDate(new Date());
            }
        }

        if (triggeredTestSession != null) {
            notificationRepository.save(notification);

            String text = templatingService.generateFromTemplate(TEST_SESSION_NOTIFICATION, ImmutableMap.of(
                "session", triggeredTestSession,
                "rules", triggeredRules));

            Job job = jobRepository.findOne(jobId);
            Set<String> fields = triggeredRules.stream().map(rule -> rule.getField()).collect(toSet());

            String subject = templatingService.generateFromTemplate(TEST_SESSION_NOTIFICATION_SUBJECT,
                ImmutableMap.of("jobName", job.getName(), "fields", fields));

            List<String> recipients = notification.getRecipients().stream()
                .map(recipient -> recipient.getEmail())
                .collect(toList());

            LOGGER.info("Test Session {} triggered rules {}, sending email to {}",
                executionId, triggeredRules, recipients);

            emailService.sendEmail(recipients, subject, text);
        }
    }

    private Object getField(Criteria rule, TestSession testSession) {
        try {
            return new PropertyDescriptor(rule.getField(), TestSession.class)
                .getReadMethod().invoke(testSession);
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            throw new ServiceException(() -> format("Failed to read test session field %s referenced by rule %s",
                rule.getField(), rule.getId()), e);
        }
    }

    private JobNotification findOrCreateJobNotification(String jobId) {
        verifyFound(jobRepository.findOne(jobId));

        JobNotification notification = notificationRepository.findByJobId(jobId);
        if (notification == null) {
            notification = new JobNotification();
            notification.setJobId(jobId);
        }
        return notification;
    }
}

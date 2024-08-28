package com.ericsson.gic.tms.tvs.presentation.controllers;

import com.ericsson.gic.tms.presentation.validation.NotFoundException;
import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.presentation.builders.RuleBuilder;
import com.ericsson.gic.tms.tvs.presentation.dto.RecipientBean;
import com.ericsson.gic.tms.tvs.presentation.dto.RuleBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         14/02/2017
 */
public class JobNotificationControllerTest extends AbstractIntegrationTest {

    private static final String JOB_ID = "d490b7d8e4b03ebcdc2b1234";
    private static final String INVALID_JOB_ID = "invalid";

    private static final String FIELD_NAME = "PassRate";
    private static final String OPERATOR = "<=";
    private static final String OPERATOR_NEW = "<";
    private static final String FIELD_VALUE = "90";
    private static final String FIELD_TYPE = "number";

    private static final String FIELD_VALUE_NEW = "70";
    private static final String EMAIL = "test@test.com";


    @Autowired
    private JobNotificationController jobNotificationController;

    private RuleBean ruleBean;

    private RecipientBean recipientBean;

    @Before
    public void setUp() throws Exception {
        ruleBean = new RuleBuilder()
            .withField(FIELD_NAME)
            .withFieldType(FIELD_TYPE)
            .withOperation(OPERATOR)
            .withValue(FIELD_VALUE)
            .build();

        recipientBean = new RecipientBean(EMAIL);
        recipientBean.setUsername("username");
    }

    @Test
    public void addRule() throws Exception {
        assertThat(jobNotificationController.getRules(JOB_ID).unwrap()).isEmpty();

        RuleBean saved = jobNotificationController.addRule(JOB_ID, ruleBean).unwrap();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved).isEqualToIgnoringGivenFields(ruleBean, "id");

        List<RuleBean> allRules = jobNotificationController.getRules(JOB_ID).unwrap();
        assertThat(allRules).hasSize(1);
        assertThat(allRules.get(0)).isEqualToComparingFieldByField(saved);
    }

    @Test
    public void addRuleWithInvalidJob() throws Exception {
        assertThatThrownBy(() ->
            jobNotificationController.addRule(INVALID_JOB_ID, ruleBean)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void addRuleWithInvalidBean() throws Exception {
        assertThatThrownBy(() ->
            jobNotificationController.addRule(JOB_ID, new RuleBean())
        ).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void updateRule() throws Exception {
        RuleBean saved = jobNotificationController.addRule(JOB_ID, ruleBean).unwrap();

        RuleBean updatedBean = new RuleBuilder()
            .withField(FIELD_NAME)
            .withFieldType(FIELD_TYPE)
            .withOperation(OPERATOR_NEW)
            .withValue(FIELD_VALUE_NEW)
            .build();

        RuleBean updated = jobNotificationController.updateRule(JOB_ID, saved.getId(), updatedBean).unwrap();

        assertThat(updated).isEqualToIgnoringGivenFields(updatedBean, "id");

        List<RuleBean> allRules = jobNotificationController.getRules(JOB_ID).unwrap();
        assertThat(allRules).hasSize(1);
        assertThat(allRules.get(0)).isEqualToComparingFieldByField(updated);
    }

    @Test
    public void removeRule() throws Exception {
        RuleBean saved = jobNotificationController.addRule(JOB_ID, ruleBean).unwrap();
        assertThat(jobNotificationController.getRules(JOB_ID).unwrap()).hasSize(1);

        jobNotificationController.removeRule(JOB_ID, saved.getId());

        assertThat(jobNotificationController.getRules(JOB_ID).unwrap()).isEmpty();
    }

    @Test
    public void addRecipient() throws Exception {
        assertThat(jobNotificationController.getRecipients(JOB_ID).unwrap()).isEmpty();

        RecipientBean saved = jobNotificationController.addRecipient(JOB_ID, recipientBean).unwrap();

        assertThat(saved).isEqualToComparingFieldByField(recipientBean);

        List<RecipientBean> recipients = jobNotificationController.getRecipients(JOB_ID).unwrap();
        assertThat(recipients).hasSize(1);
        assertThat(recipients.get(0)).isEqualToComparingFieldByField(saved);
    }

    @Test
    public void removeRecipient() throws Exception {
        jobNotificationController.addRecipient(JOB_ID, recipientBean).unwrap();
        assertThat(jobNotificationController.getRecipients(JOB_ID).unwrap()).hasSize(1);

        jobNotificationController.removeRecipient(JOB_ID, EMAIL);

        assertThat(jobNotificationController.getRecipients(JOB_ID).unwrap()).isEmpty();
    }

    @Test
    public void addRecipientWithInvalidBean() throws Exception {
        assertThatThrownBy(() ->
            jobNotificationController.addRecipient(JOB_ID, new RecipientBean("invalid-email"))
        ).isInstanceOf(ConstraintViolationException.class);
    }

}

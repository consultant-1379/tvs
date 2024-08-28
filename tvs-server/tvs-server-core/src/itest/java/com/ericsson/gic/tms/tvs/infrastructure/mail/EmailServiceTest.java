package com.ericsson.gic.tms.tvs.infrastructure.mail;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.google.common.collect.Lists;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         06/04/2017
 */
public class EmailServiceTest extends AbstractIntegrationTest {

    private static final String RECIPIENT1 = "taftest1@ericsson.com";
    private static final String RECIPIENT2 = "taftest2@ericsson.com";
    private static final String RECIPIENT3 = "taftest3@ericsson.com";

    private static final String SUBJECT = "Test subject";
    private static final String MESSAGE_TEXT = "Test message";

    @Autowired
    private EmailServiceImpl emailService;

    private GreenMail smtpServer;
    private List<String> recipients;

    @Before
    public void setUp() throws Exception {
        recipients = Lists.newArrayList(RECIPIENT1, RECIPIENT2, RECIPIENT3);

        smtpServer = new GreenMail(ServerSetupTest.SMTP);
        smtpServer.start();
    }

    @Test
    public void sendEmail() throws Exception {
        emailService.sendEmail(recipients, SUBJECT, MESSAGE_TEXT);

        List<MimeMessage> receivedMessages = Arrays.asList(smtpServer.getReceivedMessages());
        assertThat(receivedMessages).hasSize(recipients.size());

        MimeMessage message = receivedMessages.get(0);
        assertThat(message.getSubject()).isEqualTo(SUBJECT);
        assertThat(GreenMailUtil.getBody(message)).isEqualTo(MESSAGE_TEXT);

        String addressList = GreenMailUtil.getAddressList(message.getAllRecipients());
        assertThat(addressList.split(", ")).containsExactly(recipients.stream().toArray(String[]::new));
    }

    @After
    public void tearDown() throws Exception {
        smtpServer.stop();
    }
}

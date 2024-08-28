package com.ericsson.gic.tms.tvs.infrastructure.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ericsson.gic.tms.infrastructure.Profiles.DEVELOPMENT;
import static com.ericsson.gic.tms.infrastructure.Profiles.INTEGRATION_TEST;
import static com.ericsson.gic.tms.infrastructure.Profiles.TEST;

@Service
@Primary
@Profile({DEVELOPMENT, TEST, INTEGRATION_TEST})
public class EmailServiceStub implements EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceStub.class);

    @Override
    public void sendEmail(List<String> recipients, String subject, String text) {
        LOG.info("Sent email:\nTo: {}\nSubj: {}\nText: {}", recipients, subject, text);
    }
}

package com.ericsson.gic.tms.tvs.infrastructure.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ericsson.gic.tms.infrastructure.Profiles.*;
import static com.google.common.base.Strings.isNullOrEmpty;


@Service
@Profile({DEVELOPMENT, INTEGRATION_TEST, TEST, STAGE, PRODUCTION})
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(List<String> recipients, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipients.stream().filter(r -> !isNullOrEmpty(r)).toArray(size -> new String[size]));
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }
}

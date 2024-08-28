package com.ericsson.gic.tms.tvs.infrastructure.mail;

import java.util.List;

public interface EmailService {
    void sendEmail(List<String> recipients, String subject, String text);
}

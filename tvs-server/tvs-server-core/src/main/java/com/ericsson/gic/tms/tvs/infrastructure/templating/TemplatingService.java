package com.ericsson.gic.tms.tvs.infrastructure.templating;

import com.ericsson.gic.tms.presentation.validation.ServiceException;
import com.google.common.base.Charsets;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import static java.lang.String.format;

@Service
public class TemplatingService {

    public static final String TEST_SESSION_NOTIFICATION = "test-session-notification.ftl";
    public static final String TEST_SESSION_NOTIFICATION_SUBJECT = "test-session-notification-subject.ftl";
    private static final String ENCODING = Charsets.UTF_8.toString();
    private Configuration cfg;

    @PostConstruct
    void configure() {
        cfg = new Configuration(new Version("2.3.23"));

        cfg.setClassForTemplateLoading(TemplatingService.class, "/templates");
        cfg.setDefaultEncoding(ENCODING);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLocalizedLookup(false);
    }

    public String generateFromTemplate(String templateName, Map<String, Object> variables) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Template template = cfg.getTemplate(templateName);
            template.process(variables, new OutputStreamWriter(out, ENCODING));

            return out.toString(ENCODING);
        } catch (IOException | TemplateException e) {
            throw new ServiceException(() -> format("Exception processing template %s", templateName), e);
        }
    }
}

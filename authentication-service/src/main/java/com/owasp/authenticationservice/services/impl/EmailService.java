package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.config.EmailContext;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.services.IEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class EmailService implements IEmailService {

    @Value("${spring.mail.username}")
    private String noreplyMail;

    private final EmailContext _emailContext;

    public EmailService(EmailContext emailContext) {
        _emailContext = emailContext;
    }

    @Override
    public void newPasswordAnnouncementMail(SimpleUser simpleUser, String password) {
        String to = simpleUser.getUsername();
        String subject = "Password Recovery";
        Context context = new Context();
        context.setVariable("name", String.format("%s %s", simpleUser.getFirstName(), simpleUser.getLastName()));
        context.setVariable("password", String.format("%s", password));
        _emailContext.send(to, subject, "newPassword", context);
    }
}

package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.entity.SimpleUser;

public interface IEmailService {

    void newPasswordAnnouncementMail(SimpleUser simpleUser, String password);

}

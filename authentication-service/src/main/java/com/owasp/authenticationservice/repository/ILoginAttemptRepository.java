package com.owasp.authenticationservice.repository;

import com.owasp.authenticationservice.entity.BrowserFingerprint;
import com.owasp.authenticationservice.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ILoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {

    LoginAttempt findOneByBrowserFingerprint(BrowserFingerprint browserFingerprint);

}

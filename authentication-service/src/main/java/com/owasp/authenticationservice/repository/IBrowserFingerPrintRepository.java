package com.owasp.authenticationservice.repository;

import com.owasp.authenticationservice.entity.BrowserFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IBrowserFingerPrintRepository extends JpaRepository<BrowserFingerprint, UUID> {
}

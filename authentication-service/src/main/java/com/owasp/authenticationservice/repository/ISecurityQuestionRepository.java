package com.owasp.authenticationservice.repository;

import com.owasp.authenticationservice.entity.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ISecurityQuestionRepository extends JpaRepository<SecurityQuestion, UUID> {
}

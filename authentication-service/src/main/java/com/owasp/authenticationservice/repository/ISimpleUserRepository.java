package com.owasp.authenticationservice.repository;

import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ISimpleUserRepository extends JpaRepository<SimpleUser, UUID> {

    SimpleUser findOneById(UUID id);

}
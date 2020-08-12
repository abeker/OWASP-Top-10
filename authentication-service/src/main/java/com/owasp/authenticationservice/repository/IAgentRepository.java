package com.owasp.authenticationservice.repository;

import com.owasp.authenticationservice.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IAgentRepository extends JpaRepository<Agent, UUID> {

    Agent findOneById(UUID id);

}
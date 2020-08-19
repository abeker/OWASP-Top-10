package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.request.CreateAgentRequest;
import com.owasp.authenticationservice.dto.response.AgentResponse;

import java.util.UUID;

public interface IAgentService {

    AgentResponse createAgent(CreateAgentRequest request);

    AgentResponse getAgent(UUID id);
}

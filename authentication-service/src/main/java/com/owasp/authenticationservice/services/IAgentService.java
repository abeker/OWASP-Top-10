package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.request.CreateAgentRequest;
import com.owasp.authenticationservice.dto.response.AgentResponse;

public interface IAgentService {

    AgentResponse createAgent(CreateAgentRequest request);

}

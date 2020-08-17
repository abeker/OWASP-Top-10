package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.request.CreateAgentRequest;
import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.AgentResponse;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.services.impl.AgentService;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agents")
public class AgentController {

    private final AgentService _agentService;

    public AgentController(AgentService agentService) {
        _agentService = agentService;
    }

    @PostMapping("")
    public AgentResponse createAgent(@RequestBody CreateAgentRequest request) throws GeneralException {
        return _agentService.createAgent(request);
    }
}

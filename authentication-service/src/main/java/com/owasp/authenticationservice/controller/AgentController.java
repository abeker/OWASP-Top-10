package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.request.CreateAgentRequest;
import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.AgentResponse;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.services.impl.AgentService;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/{id}")
    AgentResponse getAgent(@PathVariable("id") UUID id) {
        return _agentService.getAgent(id);
    }
}

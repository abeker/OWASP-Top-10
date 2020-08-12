package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.services.impl.AgentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agents")
public class AgentController {

    private final AgentService _agentService;

    public AgentController(AgentService agentService) {
        _agentService = agentService;
    }
}

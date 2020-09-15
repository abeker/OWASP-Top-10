package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.request.CreateAgentRequest;
import com.owasp.authenticationservice.dto.response.AgentResponse;
import com.owasp.authenticationservice.services.impl.AgentService;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('CREATE_AGENT')")
    public AgentResponse createAgent(@RequestHeader("Auth-Token") String token,
                                     @RequestBody CreateAgentRequest request) throws GeneralException {
        return agentService.createAgent(request, token);
    }

    @GetMapping("/{id}")
    AgentResponse getAgent(@PathVariable("id") UUID id) {
        return agentService.getAgent(id);
    }
}

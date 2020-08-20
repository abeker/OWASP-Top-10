package com.owasp.adservice.client;

import com.owasp.adservice.dto.response.AgentResponse;
import com.owasp.adservice.dto.response.SimpleUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "auth")
public interface AuthClient {

    @GetMapping("/agents/{id}")
    AgentResponse getAgent(@PathVariable("id") UUID id);

    @GetMapping("/simple-users/{id}")
    SimpleUserResponse getSimpleUser(@PathVariable("id") UUID id);

    @GetMapping("/users/{token}/token-agent")
    AgentResponse getAgentFromToken(@PathVariable("token") String token);

    @GetMapping("/users/{token}/token-simple-user")
    SimpleUserResponse getSimpleUserFromToken(@PathVariable("token") String token);

}

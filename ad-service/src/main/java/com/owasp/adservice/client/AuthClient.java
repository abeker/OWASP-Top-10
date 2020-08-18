package com.owasp.adservice.client;

import com.owasp.adservice.dto.response.AgentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth")
public interface AuthClient {

    @GetMapping("/agents/{id}")
    AgentResponse getAgent(@PathVariable("id") UUID id);

}

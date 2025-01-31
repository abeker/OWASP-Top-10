package com.owasp.authenticationservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class AgentResponse {

    private UUID id;

    private String username;

    private String firstName;

    private String lastName;

    private String address;

    private String userRole;

}

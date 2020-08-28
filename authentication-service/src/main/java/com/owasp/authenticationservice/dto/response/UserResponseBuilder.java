package com.owasp.authenticationservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponseBuilder {

    private UUID id;

    private String username;

    private String password;

    private String userRole;

    private String firstName;

    private String lastName;

}

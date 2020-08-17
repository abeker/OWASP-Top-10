package com.owasp.authenticationservice.dto.response;

import com.owasp.authenticationservice.util.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
public class SimpleUserResponse {

    private UUID id;

    private String username;

    private String firstName;

    private String lastName;

    private String userRole;

}

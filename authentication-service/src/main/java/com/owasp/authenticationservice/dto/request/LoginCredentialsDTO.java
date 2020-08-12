package com.owasp.authenticationservice.dto.request;

import lombok.Data;

@Data
public class LoginCredentialsDTO {

    private String username;

    private String password;
}

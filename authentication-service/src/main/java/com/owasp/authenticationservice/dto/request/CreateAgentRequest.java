package com.owasp.authenticationservice.dto.request;

import lombok.Data;

@Data
public class CreateAgentRequest {

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String rePassword;

    private String address;

}

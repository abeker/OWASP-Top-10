package com.owasp.authenticationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateSimpleUserRequest {

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String rePassword;

    private String ssn;

    private String address;
}

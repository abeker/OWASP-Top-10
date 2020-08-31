package com.owasp.authenticationservice.dto.response;

import lombok.Data;

@Data
public class UserInfoResponse {

    private String username;

    private String firstName;

    private String lastName;

    private String userRole;

    private String ssn;

    private String address;

    private String securityQuestion;

}

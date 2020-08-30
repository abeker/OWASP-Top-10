package com.owasp.authenticationservice.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    private String username;

    private String securityQuestion;

}

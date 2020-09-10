package com.owasp.authenticationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginCredentialsRequest {

    private String username;

    private String password;

    private boolean dictionaryAttack;

    @JsonProperty
    private boolean isSQLI;

    private BrowserFingerprintRequest browserFingerprint;
}

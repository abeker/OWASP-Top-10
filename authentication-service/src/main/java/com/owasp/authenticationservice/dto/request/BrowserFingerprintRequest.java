package com.owasp.authenticationservice.dto.request;

import lombok.Data;

@Data
public class BrowserFingerprintRequest {

    private String fingerprint;

    private String browserName;

    private String browserVersion;

    private String os;

    private String osVersion;

    private String cpu;

    private String screenPrint;

    private String plugins;

    private String language;

    private String timeZone;

}

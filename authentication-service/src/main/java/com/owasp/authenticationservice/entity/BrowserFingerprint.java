package com.owasp.authenticationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrowserFingerprint extends BaseEntity {

    private String fingerprint;

    private String browserName;

    private String browserVersion;

    private String OS;

    private String OSVersion;

    private String CPU;

    private String screenPrint;

    private String plugins;

    private String language;

    private String timeZone;

    private String user_agent;

    private String address;

    @OneToMany(mappedBy = "browserFingerprint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LoginAttempt> loginAttempts;

}

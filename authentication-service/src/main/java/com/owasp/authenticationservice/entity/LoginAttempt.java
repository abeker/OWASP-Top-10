package com.owasp.authenticationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginAttempt extends BaseEntity {

    private LocalDateTime timeFirstMistake = LocalDateTime.now();

    private int attempts = 1;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BrowserFingerprint browserFingerprint;

}

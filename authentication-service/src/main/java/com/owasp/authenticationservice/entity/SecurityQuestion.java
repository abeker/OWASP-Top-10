package com.owasp.authenticationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SecurityQuestion extends BaseEntity {

    @OneToOne(mappedBy = "securityQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SimpleUser simpleUser;

    private String question;

    private String answer;

    private boolean deleted = false;

}

package com.owasp.authenticationservice.entity;

import com.owasp.authenticationservice.util.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SuppressWarnings("SpellCheckingInspection")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUser extends User {

    private String ssn;                 // jmbg

    private String address;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "security_question_id", referencedColumnName = "id")
    private SecurityQuestion securityQuestion;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.PENDING;

    private LocalDateTime confirmationTime = LocalDateTime.now();

}

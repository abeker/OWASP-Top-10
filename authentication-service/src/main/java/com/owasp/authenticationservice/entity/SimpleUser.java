package com.owasp.authenticationservice.entity;

import com.owasp.authenticationservice.util.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.PENDING;

    private LocalDateTime confirmationTime;

}

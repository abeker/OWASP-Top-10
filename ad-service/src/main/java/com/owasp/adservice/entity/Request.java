package com.owasp.adservice.entity;

import com.owasp.adservice.util.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request extends BaseEntity {

    @Column(name = "customer_id")
    private UUID customerID;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDate receptionDate = LocalDate.now();

    private String pickUpAddress;

    private boolean deleted = false;

    private LocalDate pickUpDate;          // datum preuzimanja

    private LocalTime pickUpTime;           // vreme preuzimanja

    private LocalDate returnDate;           // datum vracanja

    private LocalTime returnTime;           // vreme vracanja

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;
}

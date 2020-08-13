package com.owasp.adservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ad extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private Car car;

    private UUID agent;

    private boolean limitedDistance = false; //is distance which user can travel limited

    private String availableKilometersPerRent; //if distance is limited

    private int seats; //child seats

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Photo> adPhotos;

    @OneToMany(mappedBy = "ad")
    private Set<RequestAd> adRequests = new HashSet<RequestAd>();

    private LocalDate creationDate = LocalDate.now(); //date when ad was created

    private boolean deleted = false;

}

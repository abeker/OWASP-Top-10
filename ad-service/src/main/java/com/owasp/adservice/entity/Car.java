package com.owasp.adservice.entity;

import com.owasp.adservice.util.enums.FuelType;
import com.owasp.adservice.util.enums.GearshiftType;
import com.owasp.adservice.util.enums.NumberOfGears;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Car extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_model_id")
    private CarModel carModel;

    @Enumerated(EnumType.STRING)
    private GearshiftType gearshiftType;

    @Enumerated(EnumType.STRING)
    private NumberOfGears numberOfGears;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Ad ad;

    private String kilometersTraveled;

    private boolean deleted;

}

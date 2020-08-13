package com.owasp.adservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarModel extends BaseEntity {

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_brand_id")
    private CarBrand carBrand;

    private String carClass;

    @OneToMany(mappedBy = "carModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars = new ArrayList<>();

    private boolean deleted;

}

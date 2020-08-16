package com.owasp.adservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarBrand extends BaseEntity {

    private String name;

    private String country;

    @OneToMany(mappedBy = "carBrand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarModel> carModels = new ArrayList<>();

    private boolean deleted;

}

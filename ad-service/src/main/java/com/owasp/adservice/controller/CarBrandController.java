package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.CarBrandService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car-brands")
public class CarBrandController {

    private final CarBrandService _carBrandService;

    public CarBrandController(CarBrandService carBrandService) {
        _carBrandService = carBrandService;
    }
}

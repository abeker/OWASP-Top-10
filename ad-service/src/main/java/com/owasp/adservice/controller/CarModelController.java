package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.CarModelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car-models")
public class CarModelController {

    private final CarModelService _carModelService;

    public CarModelController(CarModelService carModelService) {
        _carModelService = carModelService;
    }
}

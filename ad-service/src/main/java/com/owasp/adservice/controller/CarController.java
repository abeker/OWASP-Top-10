package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.CarService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService _carService;

    public CarController(CarService carService) {
        _carService = carService;
    }
}

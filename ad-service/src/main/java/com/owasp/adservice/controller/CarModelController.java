package com.owasp.adservice.controller;

import com.owasp.adservice.dto.response.AdResponse;
import com.owasp.adservice.dto.response.CarModelResponse;
import com.owasp.adservice.services.impl.CarModelService;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/car-models")
public class CarModelController {

    private final CarModelService _carModelService;

    public CarModelController(CarModelService carModelService) {
        _carModelService = carModelService;
    }

    @GetMapping
    public List<CarModelResponse> getAds() throws GeneralException {
        return _carModelService.getCarModels();
    }
}

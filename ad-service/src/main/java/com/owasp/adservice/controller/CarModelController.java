package com.owasp.adservice.controller;

import com.owasp.adservice.dto.response.CarModelResponse;
import com.owasp.adservice.services.ICarModelService;
import com.owasp.adservice.services.impl.CarModelService;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/car-models")
public class CarModelController {

    private final ICarModelService _carModelService;

    public CarModelController(CarModelService carModelService) {
        _carModelService = carModelService;
    }

    @GetMapping
    public List<CarModelResponse> getAds(@RequestHeader("Auth-Token") String token) throws GeneralException {
        return _carModelService.getCarModels(token);
    }
}

package com.owasp.adservice.services;

import com.owasp.adservice.dto.response.CarModelResponse;

import java.util.List;

public interface ICarModelService {

    List<CarModelResponse> getCarModels();

}

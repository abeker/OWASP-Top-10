package com.owasp.adservice.services.impl;

import com.owasp.adservice.dto.response.CarModelResponse;
import com.owasp.adservice.entity.CarModel;
import com.owasp.adservice.repository.ICarModelRepository;
import com.owasp.adservice.services.ICarModelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarModelService implements ICarModelService {

    private final ICarModelRepository _carModelRepository;

    public CarModelService(ICarModelRepository carModelRepository) {
        _carModelRepository = carModelRepository;
    }

    @Override
    public List<CarModelResponse> getCarModels() {
        List<CarModel> carModels = _carModelRepository.findAllByDeleted(false);
        return carModels.stream()
                .map(carModel -> mapCarModelToCarModelResponse(carModel))
                .collect(Collectors.toList());
    }

    private CarModelResponse mapCarModelToCarModelResponse(CarModel carModel) {
        CarModelResponse response = new CarModelResponse();
        response.setId(carModel.getId());
        response.setBrandName(carModel.getCarBrand().getName());
        response.setModelName(carModel.getName());
        response.setClassName(carModel.getCarClass());
        response.setBrandCountry(carModel.getCarBrand().getCountry());

        return response;
    }

}

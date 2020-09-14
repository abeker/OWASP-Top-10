package com.owasp.adservice.services.impl;

import com.owasp.adservice.client.AuthClient;
import com.owasp.adservice.dto.response.CarModelResponse;
import com.owasp.adservice.entity.CarModel;
import com.owasp.adservice.repository.ICarModelRepository;
import com.owasp.adservice.services.ICarModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarModelService implements ICarModelService {

    private final Logger logger = LoggerFactory.getLogger(CarModelService.class);

    private final ICarModelRepository _carModelRepository;
    private final AuthClient _authClient;

    public CarModelService(ICarModelRepository carModelRepository, AuthClient authClient) {
        _carModelRepository = carModelRepository;
        _authClient = authClient;
    }

    @Override
    public List<CarModelResponse> getCarModels(String token) {
        logger.info("[{}] retrieve car-models", _authClient.getCurrentUser(token));
        List<CarModel> carModels = _carModelRepository.findAllByDeleted(false);
        return carModels.stream()
                .map(this::mapCarModelToCarModelResponse)
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

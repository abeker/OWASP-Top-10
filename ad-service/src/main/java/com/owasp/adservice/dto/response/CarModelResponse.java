package com.owasp.adservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class CarModelResponse {

    private UUID id;

    private String modelName;

    private String brandName;

    private String brandCountry;

    private String className;

}

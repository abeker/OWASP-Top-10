package com.owasp.adservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class CarResponse {

    private UUID id;

    private CarModelResponse carModel;

    private String gearshiftType;

    private String numberOfGears;

    private String fuelType;

    private String kilometersTravelled;

}

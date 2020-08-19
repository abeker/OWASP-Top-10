package com.owasp.adservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddAdRequest {

    private String carModel;        // Audi A3

    private String gearshiftType;

    private String fuelType;

    private UUID agentId;

    private boolean limitedDistance;

    private String availableKilometersPerRent;

    private String kilometersTraveled;

    private int seats;

    private boolean simpleUser;
}

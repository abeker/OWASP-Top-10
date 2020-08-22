package com.owasp.adservice.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AdResponse {

    private UUID id;

    private CarResponse car;

    private AgentResponse agent;

    private List<PhotoResponse> photos;

    private boolean limitedDistance;

    private String availableKilometersPerRent;

    private int seats;

    private LocalDate creationDate;

    private int numberOfRequests;

}

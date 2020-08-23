package com.owasp.adservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class AdRequestResponse {

    private UUID id;

    private AdResponse ad;

    private String pickUpDate;          // datum preuzimanja

    private String pickUpTime;           // vreme preuzimanja

    private String returnDate;           // datum vracanja

    private String returnTime;           // vreme vracanja

    private String requestStatus;

    private String pickUpAddress;

    private AgentResponse agent;

    private SimpleUserResponse simpleUser;

    private String averageRate;

}

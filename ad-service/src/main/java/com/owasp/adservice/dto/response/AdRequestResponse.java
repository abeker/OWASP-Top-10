package com.owasp.adservice.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AdRequestResponse {

    private AdResponse ad;

    private String requestStatus;

    private String pickUpAddress;

}

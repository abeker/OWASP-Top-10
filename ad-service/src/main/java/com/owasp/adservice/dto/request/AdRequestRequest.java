package com.owasp.adservice.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class AdRequestRequest {

    private UUID adID;

    private UUID customerID;

    private String pickUpDate;      // format -> "2016-06-12"

    private String pickUpTime;      // format -> "06:30"

    private String returnDate;

    private String returnTime;

    private String pickUpAddress;

}

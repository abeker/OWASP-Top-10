package com.owasp.adservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class PhotoResponse {

    private UUID id;

    private String name;

    private String type;

    private byte[] picByte;

}

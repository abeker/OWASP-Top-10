package com.owasp.adservice.dto.request;

import lombok.Data;

@Data
public class UnsafeUserRequest {

    private String requestStatus;

    private String customer_id;

}

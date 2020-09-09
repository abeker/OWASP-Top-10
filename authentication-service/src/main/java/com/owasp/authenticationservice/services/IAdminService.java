package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.response.SimpleUserResponse;

import java.util.List;
import java.util.UUID;

public interface IAdminService {
    List<SimpleUserResponse> approveRegistrationRequest(UUID id, String token);

    List<SimpleUserResponse> denyRegistrationRequest(UUID id, String token);
}

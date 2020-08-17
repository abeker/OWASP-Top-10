package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import java.util.List;

public interface ISimpleUserService {

    SimpleUserResponse createSimpleUser(CreateSimpleUserRequest request);

    List<SimpleUserResponse> getSimpleUserByStatus(String userStatus);
}

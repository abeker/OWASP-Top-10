package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.dto.response.UserInfoResponse;
import com.owasp.authenticationservice.dto.response.UserResponse;

public interface IUserService {

    UserInfoResponse getUserInfo(String username);
}

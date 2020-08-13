package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.request.LoginCredentialsDTO;
import com.owasp.authenticationservice.dto.response.UserResponse;

import javax.servlet.http.HttpServletRequest;

public interface IAuthService {

    UserResponse login(LoginCredentialsDTO request, HttpServletRequest httpServletRequest);

    String getPermission(String token);

}

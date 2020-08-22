package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.request.LoginCredentialsDTO;
import com.owasp.authenticationservice.dto.response.AgentResponse;
import com.owasp.authenticationservice.dto.response.UserResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface IAuthService {

    UserResponse login(LoginCredentialsDTO request, HttpServletRequest httpServletRequest);

    String getPermission(String token);

    UserResponse getUser(UUID userId);

    UserResponse getUserByEmail(String userEmail);
}

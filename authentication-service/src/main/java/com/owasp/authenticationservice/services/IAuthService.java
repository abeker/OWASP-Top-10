package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.request.BrowserFingerprintRequest;
import com.owasp.authenticationservice.dto.request.LoginCredentialsRequest;
import com.owasp.authenticationservice.dto.response.UserResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public interface IAuthService {

    UserResponse login(LoginCredentialsRequest request, HttpServletRequest httpServletRequest) throws SQLException;

    String getPermission(String token);

    UserResponse getUser(UUID userId);

    UserResponse getUserByEmail(String userEmail);

    boolean checkPassword(String userPassword) throws IOException;

    boolean canAgainLogin(BrowserFingerprintRequest browserFingerprint, HttpServletRequest httpServletRequest);
}

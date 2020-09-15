package com.owasp.authenticationservice.services;

import com.owasp.authenticationservice.dto.request.BrowserFingerprintRequest;
import com.owasp.authenticationservice.dto.request.ChangePasswordRequest;
import com.owasp.authenticationservice.dto.request.LoginCredentialsRequest;
import com.owasp.authenticationservice.dto.response.UserQuestionResponse;
import com.owasp.authenticationservice.dto.response.UserResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
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

    boolean checkSecurityQuestion(String token, String answer);

    boolean changePassword(ChangePasswordRequest changePasswordRequest);

    boolean isPasswordWeak(String password, File file) throws FileNotFoundException;

    UserQuestionResponse getUserQuestionByEmail(String userEmail);

    void invalidateSession(HttpServletRequest request, HttpServletResponse response);
}

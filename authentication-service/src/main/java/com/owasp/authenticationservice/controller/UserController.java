package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.request.BrowserFingerprintRequest;
import com.owasp.authenticationservice.dto.request.ChangePasswordRequest;
import com.owasp.authenticationservice.dto.request.LoginCredentialsRequest;
import com.owasp.authenticationservice.dto.response.*;
import com.owasp.authenticationservice.security.TokenUtils;
import com.owasp.authenticationservice.services.IAgentService;
import com.owasp.authenticationservice.services.IAuthService;
import com.owasp.authenticationservice.services.ISimpleUserService;
import com.owasp.authenticationservice.services.impl.UserService;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@SuppressWarnings({"unused", "RedundantThrows", "FieldCanBeLocal"})
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final IAuthService authService;
    private final TokenUtils tokenUtils;
    private final IAgentService agentService;
    private final ISimpleUserService simpleUserService;

    public UserController(UserService userService, IAuthService authService, TokenUtils tokenUtils,
                          IAgentService agentService, ISimpleUserService simpleUserService) {
        this.userService = userService;
        this.authService = authService;
        this.tokenUtils = tokenUtils;
        this.agentService = agentService;
        this.simpleUserService = simpleUserService;
    }

    @GetMapping("/verify")
    public String verify(@RequestHeader("Auth-Token") String token) throws NotFoundException {
        return tokenUtils.getUsernameFromToken(token);
    }

    @GetMapping("/permission")
    public String getPermissions(@RequestHeader("Auth-Token") String token) throws NotFoundException {
        return authService.getPermission(token);
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>("Hello from auth service", HttpStatus.OK);
    }

    @PutMapping("/login")
    public UserResponse login(@RequestBody LoginCredentialsRequest request, HttpServletRequest httpServletRequest)
            throws GeneralException, SQLException {
        return authService.login(request, httpServletRequest);
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.invalidateSession(request, response);
    }

    @PutMapping("/check-attempts")
    public boolean checkAttempts(@RequestBody BrowserFingerprintRequest browserFingerprint,
                                 HttpServletRequest httpServletRequest) throws GeneralException, SQLException {
        return authService.canAgainLogin(browserFingerprint, httpServletRequest);
    }

    @PutMapping("/change-password")
    public boolean changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return authService.changePassword(changePasswordRequest);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable("id") UUID userId) throws GeneralException {
        return authService.getUser(userId);
    }

    @GetMapping("/{email}/mail")
    public UserResponse getUserByEmail(@PathVariable("email") String userEmail) throws GeneralException {
        return authService.getUserByEmail(userEmail);
    }

    @GetMapping("/{email}/security-question")
    public UserQuestionResponse getUserQuestionByEmail(@PathVariable("email") String userEmail)
            throws GeneralException {
        return authService.getUserQuestionByEmail(userEmail);
    }


    @GetMapping("/check-password/{password}")
    public boolean checkPassword(@PathVariable("password") String userPassword) throws GeneralException, IOException {
        return authService.checkPassword(userPassword);
    }

    @GetMapping("/{username}/info")
    public UserInfoResponse getUserInfo(@RequestHeader("Auth-Token") String token,
                                        @PathVariable("username") String username) {
        return userService.getUserInfo(username);
    }

    @GetMapping("/{token}/token-agent")
    public AgentResponse getAgentFromToken(@PathVariable("token") String token) throws GeneralException {
        return agentService.getAgentFromToken(token);
    }

    @GetMapping("/{token}/token-simple-user")
    public SimpleUserResponse getSimpleUserFromToken(@PathVariable("token") String token) throws GeneralException {
        return simpleUserService.getSimpleUserFromToken(token);
    }

    @GetMapping("/{token}/current-user")
    public String getCurrentUser(@PathVariable("token") String token) throws GeneralException {
        return userService.getCurrentUser(token);
    }


}

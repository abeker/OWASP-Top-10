package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.request.LoginCredentialsDTO;
import com.owasp.authenticationservice.dto.response.AgentResponse;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.dto.response.UserResponse;
import com.owasp.authenticationservice.security.TokenUtils;
import com.owasp.authenticationservice.services.IAgentService;
import com.owasp.authenticationservice.services.IAuthService;
import com.owasp.authenticationservice.services.ISimpleUserService;
import com.owasp.authenticationservice.services.impl.UserService;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService _userService;
    private final IAuthService _authService;
    private final TokenUtils _tokenUtils;
    private final IAgentService _agentService;
    private final ISimpleUserService _simpleUserService;

    public UserController(UserService userService, IAuthService authService, TokenUtils tokenUtils, IAgentService agentService, ISimpleUserService simpleUserService) {
        _userService = userService;
        _authService = authService;
        _tokenUtils = tokenUtils;
        _agentService = agentService;
        _simpleUserService = simpleUserService;
    }

    @GetMapping("/verify")
    public String verify(@RequestHeader("Auth-Token") String token) throws NotFoundException {
        return _tokenUtils.getUsernameFromToken(token);
    }

    @GetMapping("/permission")
    public String getPermissions(@RequestHeader("Auth-Token") String token) throws NotFoundException {
        return _authService.getPermission(token);
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello(){
        return new ResponseEntity<>("Hello from auth service", HttpStatus.OK);
    }

    @PutMapping("/login")
    public UserResponse login(@RequestBody LoginCredentialsDTO request, HttpServletRequest httpServletRequest) throws GeneralException {
        return _authService.login(request, httpServletRequest);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable("id") UUID userId) throws GeneralException {
        return _authService.getUser(userId);
    }

    @GetMapping("/{email}/mail")
    public UserResponse getUserByEmail(@PathVariable("email") String userEmail) throws GeneralException {
        return _authService.getUserByEmail(userEmail);
    }

    @GetMapping("/{token}/token-agent")
    public AgentResponse getAgentFromToken(@PathVariable("token") String token) throws GeneralException {
        return _agentService.getAgentFromToken(token);
    }

    @GetMapping("/{token}/token-simple-user")
    public SimpleUserResponse getSimpleUserFromToken(@PathVariable("token") String token) throws GeneralException {
        return _simpleUserService.getSimpleUserFromToken(token);
    }
}

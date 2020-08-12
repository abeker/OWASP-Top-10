package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.request.LoginCredentialsDTO;
import com.owasp.authenticationservice.dto.response.UserResponse;
import com.owasp.authenticationservice.services.impl.AuthService;
import com.owasp.authenticationservice.services.impl.UserService;
import com.owasp.authenticationservice.util.exception.GeneralException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService _userService;
    private final AuthService _authService;

    public UserController(UserService userService, AuthService authService) {
        _userService = userService;
        _authService = authService;
    }

    @PutMapping("/login")
    public UserResponse login(@RequestBody LoginCredentialsDTO request, HttpServletRequest httpServletRequest) throws GeneralException {
        return _authService.login(request, httpServletRequest);
    }
}

package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.services.impl.SimpleUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simple-users")
public class SimpleUserController {

    private final SimpleUserService _simpleUserService;

    public SimpleUserController(SimpleUserService simpleUserService) {
        _simpleUserService = simpleUserService;
    }
}

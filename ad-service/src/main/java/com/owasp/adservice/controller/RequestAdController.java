package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.RequestAdService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request-ads")
public class RequestAdController {

    private final RequestAdService _requestAdService;

    public RequestAdController(RequestAdService requestAdService) {
        _requestAdService = requestAdService;
    }
}

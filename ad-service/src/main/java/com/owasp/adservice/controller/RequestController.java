package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.RequestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService _requestService;

    public RequestController(RequestService requestService) {
        _requestService = requestService;
    }
}

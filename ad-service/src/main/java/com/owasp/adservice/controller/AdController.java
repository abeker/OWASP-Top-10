package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.AdService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ads")
public class AdController {

    private final AdService _adService;

    public AdController(AdService adService) {
        _adService = adService;
    }
}

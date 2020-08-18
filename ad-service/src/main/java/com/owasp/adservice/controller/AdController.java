package com.owasp.adservice.controller;

import com.owasp.adservice.dto.response.AdResponse;
import com.owasp.adservice.services.impl.AdService;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdController {

    private final AdService _adService;

    public AdController(AdService adService) {
        _adService = adService;
    }

    @GetMapping
    public List<AdResponse> getAds() throws GeneralException {
        return _adService.getAds(false, null);
    }
}

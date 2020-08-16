package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.RatingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService _ratingService;

    public RatingController(RatingService ratingService) {
        _ratingService = ratingService;
    }
}

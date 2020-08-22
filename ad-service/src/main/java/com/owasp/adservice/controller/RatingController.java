package com.owasp.adservice.controller;

import com.owasp.adservice.services.IRatingService;
import com.owasp.adservice.services.impl.RatingService;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final IRatingService _ratingService;

    public RatingController(IRatingService ratingService) {
        _ratingService = ratingService;
    }

    @PostMapping("/{grade}")
    @PreAuthorize("hasAuthority('POST_RATE')")
    public void addRate(@RequestHeader("Auth-Token") String token,
                        @RequestBody String adId,
                        @PathVariable("grade") String grade) throws GeneralException {
        _ratingService.addRate(UUID.fromString(adId), token, grade);
    }
}

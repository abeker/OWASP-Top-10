package com.owasp.adservice.controller;

import com.owasp.adservice.dto.request.AddAdRequest;
import com.owasp.adservice.dto.response.AdResponse;
import com.owasp.adservice.services.impl.AdService;
import com.owasp.adservice.util.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@SuppressWarnings("unused")
@CrossOrigin(origins = "http://localhost:4200")
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

    @PostMapping(consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('CREATE_AD')")
    public ResponseEntity<?> createAd(@RequestHeader("Auth-Token") String token,
                                      @RequestPart("imageFile") List<MultipartFile> fileList,
                                      @RequestPart("request") AddAdRequest request) {
        return new ResponseEntity<>(_adService.createAd(fileList, request, token), HttpStatus.CREATED);
    }

    @GetMapping("/{agentId}/ads")
    public List<AdResponse> getAgentAds(@RequestHeader("Auth-Token") String token,
                                        @PathVariable("agentId") String agentId) throws GeneralException {
        return _adService.getAgentAds(false, token);
    }
}

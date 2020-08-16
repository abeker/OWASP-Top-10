package com.owasp.adservice.controller;

import com.owasp.adservice.services.impl.PhotoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService _photoService;

    public PhotoController(PhotoService photoService) {
        _photoService = photoService;
    }
}

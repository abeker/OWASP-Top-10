package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.services.impl.AdminService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService _adminService;

    public AdminController(AdminService adminService) {
        _adminService = adminService;
    }
}

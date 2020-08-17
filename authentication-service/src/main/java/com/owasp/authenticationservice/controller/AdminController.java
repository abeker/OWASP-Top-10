package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.services.impl.AdminService;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService _adminService;

    public AdminController(AdminService adminService) {
        _adminService = adminService;
    }

    @PutMapping("/approve")
    public List<SimpleUserResponse> approveRegistrationRequest(@RequestBody String id) throws GeneralException {
        return _adminService.approveRegistrationRequest(UUID.fromString(id));
    }

    @PutMapping("/deny")
    public List<SimpleUserResponse> denyRegistrationRequest(@RequestBody String id) throws GeneralException {
        return _adminService.denyRegistrationRequest(UUID.fromString(id));
    }
}

package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.services.impl.AdminService;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/approve")
    @PreAuthorize("hasAuthority('APPROVE_USER_REQUEST')")
    public List<SimpleUserResponse> approveRegistrationRequest(@RequestHeader("Auth-Token") String token,
                                                               @RequestBody String id) throws GeneralException {
        return adminService.approveRegistrationRequest(UUID.fromString(id), token);
    }

    @PutMapping("/deny")
    @PreAuthorize("hasAuthority('DENY_USER_REQUEST')")
    public List<SimpleUserResponse> denyRegistrationRequest(@RequestHeader("Auth-Token") String token,
                                                            @RequestBody String id) throws GeneralException {
        return adminService.denyRegistrationRequest(UUID.fromString(id), token);
    }
}

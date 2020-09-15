package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.services.impl.SimpleUserService;
import com.owasp.authenticationservice.services.impl.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/simple-users")
public class SimpleUserController {

    private final SimpleUserService simpleUserService;
    private final UserService userService;

    public SimpleUserController(SimpleUserService simpleUserService, UserService userService) {
        this.simpleUserService = simpleUserService;
        this.userService = userService;
    }

    @PostMapping("")
    public SimpleUserResponse createSimpleUser(@Valid @RequestBody CreateSimpleUserRequest request) {
        return simpleUserService.createSimpleUser(request);
    }

    @GetMapping("/{id}")
    SimpleUserResponse getSimpleUser(@PathVariable("id") UUID id) {
        return simpleUserService.getSimpleUser(id);
    }

    @GetMapping("/{userStatus}/status")
    public List<SimpleUserResponse> getSimpleUserByStatus(@RequestHeader("Auth-Token") String token,
                                                          @PathVariable("userStatus") String userStatus) {
        return simpleUserService.getSimpleUserByStatus(userStatus, token);
    }

    @PostMapping("/{id}/add-roles")
    public void addRolesAfterPay(@PathVariable("id") UUID userId) {
        simpleUserService.addRolesAfterPay(userId);
    }

}

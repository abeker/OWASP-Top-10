package com.owasp.authenticationservice.controller;

import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.services.impl.SimpleUserService;
import com.owasp.authenticationservice.services.impl.UserService;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/simple-users")
public class SimpleUserController {

    private final SimpleUserService _simpleUserService;
    private final UserService _userService;

    public SimpleUserController(SimpleUserService simpleUserService, UserService userService) {
        _simpleUserService = simpleUserService;
        _userService = userService;
    }

    @PostMapping("")
    public SimpleUserResponse createSimpleUser(@RequestBody CreateSimpleUserRequest request) throws GeneralException {
        return _simpleUserService.createSimpleUser(request);
    }

    @GetMapping("/{userStatus}")
    public List<SimpleUserResponse> getSimpleUserByStatus(@PathVariable("userStatus") String userStatus) {
        return _simpleUserService.getSimpleUserByStatus(userStatus);
    }
}

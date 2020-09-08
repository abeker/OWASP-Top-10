package com.owasp.Zuul;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth")
public interface AuthClient {

    @GetMapping("/users/verify")
    String verify(@RequestHeader("Auth-Token") String token);

    @GetMapping("/users/permission")
    String getPermission(@RequestHeader("Auth-Token") String token);
}

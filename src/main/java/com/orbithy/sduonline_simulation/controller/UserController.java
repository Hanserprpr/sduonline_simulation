package com.orbithy.sduonline_simulation.controller;

import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/api/me")
    public ResponseEntity<Result> me(@AuthenticationPrincipal OidcUser user) {
        return userService.getMe(user);

    }

    @GetMapping("/api/getId")
    public String getUserId(@AuthenticationPrincipal OidcUser oidcUser) {
        return oidcUser.getSubject();
    }
}

package com.orbithy.sduonline_simulation.controller;

import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@CrossOrigin
@RequestMapping("/api")
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Result> me(@AuthenticationPrincipal OidcUser user) {
        return userService.getMe(user);

    }

    @GetMapping("/getId")
    public String getUserId(@AuthenticationPrincipal OidcUser oidcUser) {
        return oidcUser.getSubject();
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(302)
                .location(URI.create("/oauth2/authorization/casdoor"))
                .build();
}}

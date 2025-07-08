package com.orbithy.sduonline_simulation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/api/me")
    public Map<String, Object> me(@AuthenticationPrincipal OidcUser user) {
        Map<String, Object> result = new HashMap<>();
        result.put("sub", user.getSubject());
        result.put("name", user.getAttributes().get("displayName"));
        result.put("email", user.getEmail());
        result.put("avatar", user.getAttributes().get("avatar"));
        return result;
    }

    @GetMapping("/api/getId")
    public String getUserId(@AuthenticationPrincipal OidcUser oidcUser) {
        return oidcUser.getSubject();
    }
}

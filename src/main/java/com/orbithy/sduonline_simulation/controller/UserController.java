package com.orbithy.sduonline_simulation.controller;

import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<Result> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Result.error(401, "用户未认证，请先登录"));
        }

        // 处理两种认证方式
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser) {
            // OAuth2 Login 方式
            return userService.getMe((OidcUser) principal);
        } else if (principal instanceof Jwt jwt) {
            // JWT Resource Server 方式
            return userService.getMeFromJwt(jwt);
        } else {
            return ResponseEntity.status(401)
                    .body(Result.error(401, "不支持的认证方式"));
        }
    }

    @GetMapping("/getId")
    public ResponseEntity<Result> getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Result.error(401, "用户未认证，请先登录"));
        }

        Object principal = authentication.getPrincipal();
        String subject = null;

        if (principal instanceof OidcUser) {
            subject = ((OidcUser) principal).getSubject();
        } else if (principal instanceof Jwt) {
            subject = ((Jwt) principal).getSubject();
        }

        if (subject == null) {
            return ResponseEntity.status(401)
                    .body(Result.error(401, "无法获取用户ID"));
        }

        return ResponseEntity.ok(Result.success(subject, "获取用户ID成功"));
    }

    @GetMapping("/debug-auth")
    public ResponseEntity<Result> debugAuth(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(Result.success("Authentication is null", "调试信息"));
        }

        Object principal = authentication.getPrincipal();
        String principalType = principal != null ? principal.getClass().getSimpleName() : "null";
        String authType = authentication.getClass().getSimpleName();

        return ResponseEntity.ok(Result.success(
            "Auth Type: " + authType + ", Principal Type: " + principalType +
            ", Authenticated: " + authentication.isAuthenticated(),
            "调试信息"));
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(302)
                .location(URI.create("/oauth2/authorization/casdoor"))
                .build();
}}

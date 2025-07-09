package com.orbithy.sduonline_simulation.controller;

import com.orbithy.sduonline_simulation.annotation.sub;
import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.service.ProgressService;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/begin")
    public ResponseEntity<Result> begin(@sub String sub, @RequestParam(required = false) String level, HttpServletRequest request) {
        return progressService.begin(sub, level, request);
    }

    @PostMapping("/end")
    public ResponseEntity<Result> end(@sub String sub, @RequestParam(required = false) String level, HttpServletRequest request) {
        return progressService.end(sub, level, request);
    }
}

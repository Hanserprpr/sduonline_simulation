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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/begin")
    public ResponseEntity<Result> begin(@sub String sub, HttpServletRequest request) {
        if (sub == null || sub.trim().isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Result.error(401, "用户未认证，请先登录"));
        }
        return progressService.begin(sub, request);
    }

    @PostMapping("/end")
    public ResponseEntity<Result> end(@sub String sub, @RequestParam(required = false) String level, HttpServletRequest request) {
        if (sub == null || sub.trim().isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Result.error(401, "用户未认证，请先登录"));
        }
        return progressService.end(sub, level, request);
    }

    // 更改游戏状态接口：可修改items、total、status、orderTime、totalDevTime、preparationProgress
    @PostMapping("/update-game-status")
    public ResponseEntity<Result> updateGameStatus(@RequestParam Long orderId,
                                                   @RequestParam(required = false) String items,
                                                   @RequestParam(required = false) Integer total,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) LocalDateTime orderTime,
                                                   @RequestParam(required = false) Integer totalDevTime,
                                                   @RequestParam(required = false) Integer preparationProgress,
                                                   HttpServletRequest request) {
        return progressService.updateGameStatus(orderId, items, total, status, orderTime, totalDevTime, preparationProgress, request);
    }
}

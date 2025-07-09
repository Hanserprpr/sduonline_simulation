package com.orbithy.sduonline_simulation.controller;

import com.orbithy.sduonline_simulation.annotation.sub;
import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.mapper.CoinMapper;
import com.orbithy.sduonline_simulation.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class UserController {
    @Resource
    private UserService userService;
    @Autowired
    private CoinMapper coinMapper;

    @GetMapping("/me")
    public ResponseEntity<Result> me(@AuthenticationPrincipal OidcUser user) {
        return userService.getMe(user);

    }

    @GetMapping("/getId")
    public String getUserId(@AuthenticationPrincipal OidcUser oidcUser) {
        return oidcUser.getSubject();
    }

    @GetMapping("/coins")
    public ResponseEntity<Result> getCoins(@sub String sub) {

        if (sub == null || sub.isEmpty()) {
            return ResponseEntity.badRequest().body(Result.error(403, "用户ID (sub) 缺失"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("coins", coinMapper.findBySub(sub));

        return ResponseEntity.ok(Result.success(response, "获取成功"));
    }
}

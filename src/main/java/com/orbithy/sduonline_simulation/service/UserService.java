package com.orbithy.sduonline_simulation.service;

import com.orbithy.sduonline_simulation.data.po.Coins;
import com.orbithy.sduonline_simulation.data.po.User;
import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.mapper.CoinMapper;
import com.orbithy.sduonline_simulation.mapper.UserMapper;
import com.orbithy.sduonline_simulation.utils.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final CoinMapper coinMapper;

    public UserService(UserMapper userMapper, CoinMapper coinMapper) {
        this.userMapper = userMapper;
        this.coinMapper = coinMapper;
    }

    public ResponseEntity<Result> getMe(OidcUser oidcuser) {
        String sub = oidcuser.getSubject();
        if (sub == null || sub.isEmpty()) {
            throw new IllegalArgumentException("User ID (sub) is missing");
        }
        User user = new User();
        if (userMapper.findByCasdoorSub(sub) == null) {
            user.setId(null);
            user.setCasdoorSub(sub);
            user.setUsername(oidcuser.getAttributes().get("displayName").toString());
            user.setEmail(StringUtils.hasText(oidcuser.getEmail()) ? oidcuser.getEmail() : null);
            user.setAvatar(oidcuser.getAttributes().get("avatar").toString());
            userMapper.insert(user);
            Coins coins = new Coins();
            coins.setSub(sub);
            coins.setCoins(0);
            coinMapper.insert(coins);
        } else {
            user = userMapper.findByCasdoorSub(sub);
        }

        return ResponseUtil.build(Result.success(user, "获取用户信息成功"));
    }
}

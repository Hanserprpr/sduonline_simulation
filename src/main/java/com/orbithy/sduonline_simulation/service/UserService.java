package com.orbithy.sduonline_simulation.service;

import com.orbithy.sduonline_simulation.data.po.Temp;
import com.orbithy.sduonline_simulation.data.po.User;
import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.mapper.TempMapper;
import com.orbithy.sduonline_simulation.mapper.UserMapper;
import com.orbithy.sduonline_simulation.utils.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final TempMapper tempMapper;

    public UserService(UserMapper userMapper, TempMapper tempMapper) {
        this.userMapper = userMapper;
        this.tempMapper = tempMapper;
    }

    @Transactional
    public ResponseEntity<Result> getMe(OidcUser oidcuser) {
        if (oidcuser == null) {
            return ResponseUtil.build(Result.error(401, "用户未认证"));
        }
        String sub = oidcuser.getSubject();
        if (sub == null || sub.isEmpty()) {
            return ResponseUtil.build(Result.error(400, "用户ID (sub) 缺失"));
        }
        User user = new User();
        if (userMapper.findByCasdoorSub(sub) == null) {
            user.setId(null);
            user.setSDUId(sub);
            user.setUsername(oidcuser.getAttributes().get("displayName").toString());
            user.setEmail(StringUtils.hasText(oidcuser.getEmail()) ? oidcuser.getEmail() : null);
            user.setAvatar(oidcuser.getAttributes().get("avatar").toString());
            userMapper.insert(user);
            Temp temp = new Temp();
            temp.setSDUId(sub);
            tempMapper.insert(temp);
        } else {
            user = userMapper.findByCasdoorSub(sub);
        }

        return ResponseUtil.build(Result.success(user, "获取用户信息成功"));
    }

    @Transactional
    public ResponseEntity<Result> getMeFromJwt(Jwt jwt) {
        if (jwt == null) {
            return ResponseUtil.build(Result.error(401, "用户未认证"));
        }
        String sub = jwt.getSubject();
        if (sub == null || sub.isEmpty()) {
            return ResponseUtil.build(Result.error(400, "用户ID (sub) 缺失"));
        }
        User user = new User();
        if (userMapper.findByCasdoorSub(sub) == null) {
            user.setId(null);
            user.setSDUId(sub);
            // 从 JWT claims 中获取用户信息
            user.setUsername(jwt.getClaimAsString("displayName"));
            user.setEmail(jwt.getClaimAsString("email"));
            user.setAvatar(jwt.getClaimAsString("avatar"));
            userMapper.insert(user);
            Temp temp = new Temp();
            temp.setSDUId(sub);
            tempMapper.insert(temp);
        } else {
            user = userMapper.findByCasdoorSub(sub);
        }

        return ResponseUtil.build(Result.success(user, "获取用户信息成功"));
    }
}

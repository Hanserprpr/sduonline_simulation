package com.orbithy.sduonline_simulation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.sduonline_simulation.data.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User findByUsername(String username);

    User findByEmail(String email);

    @Select("SELECT * FROM users WHERE casdoor_sub = #{casdoorSub}")
    User findByCasdoorSub(String casdoorSub);

}

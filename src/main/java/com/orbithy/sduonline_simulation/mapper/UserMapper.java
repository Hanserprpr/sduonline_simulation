package com.orbithy.sduonline_simulation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.sduonline_simulation.data.po.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User findByUsername(String username);

    User findByEmail(String email);

    @Select("SELECT * FROM users WHERE casdoor_sub = #{casdoorSub}")
    User findByCasdoorSub(String casdoorSub);
    
//    /**
//     * 获取用户排行榜数据
//     * 根据coins表中的coins总量排序
//     * @return 用户列表，按coins总量降序排序
//     */
//    @Select("SELECT u.*, IFNULL(c.coins, 0) as score FROM users u LEFT JOIN coins c ON u.casdoor_sub = c.sub ORDER BY score DESC")
//    List<User> getUserRankList();
}

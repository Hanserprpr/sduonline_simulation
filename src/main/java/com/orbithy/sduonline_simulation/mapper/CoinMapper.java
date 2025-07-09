package com.orbithy.sduonline_simulation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.sduonline_simulation.data.po.Coins;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CoinMapper extends BaseMapper<Coins> {
    @Select("SELECT * FROM coins WHERE sub = #{sub}")
    Coins findBySub(String sub);
}

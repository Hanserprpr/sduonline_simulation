package com.orbithy.sduonline_simulation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.sduonline_simulation.data.po.Temp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TempMapper extends BaseMapper<Temp> {
    @Select("SELECT completed_levels FROM temp WHERE sub = #{sub}")
    Temp findBySub(String sub);
}

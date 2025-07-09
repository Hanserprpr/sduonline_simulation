package com.orbithy.sduonline_simulation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.sduonline_simulation.data.po.ScoreRecords;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper extends BaseMapper<ScoreRecords> {
}

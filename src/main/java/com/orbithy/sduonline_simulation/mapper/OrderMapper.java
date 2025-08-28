package com.orbithy.sduonline_simulation.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.sduonline_simulation.data.po.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
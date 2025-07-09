package com.orbithy.sduonline_simulation.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.orbithy.sduonline_simulation.config.JsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("temp")
public class Temp {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String sub;
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> completed_levels;
    private long total_duration;
}

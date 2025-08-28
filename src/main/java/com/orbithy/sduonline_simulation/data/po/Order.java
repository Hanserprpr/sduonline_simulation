package com.orbithy.sduonline_simulation.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;                     // 主键
    private Integer SDUId;               // 外键，关联用户
    private String customerId;           // 客户ID
    private String customerName;         // 客户姓名
    private Integer price;            // 价格
    private String items;                // items JSON
    private Integer total;               // 总数/总分
    private String status;               // 状态
    private LocalDateTime orderTime;     // 下单时间
    private Integer totalDevTime;        // 总开发时长
    private Integer preparationProgress; // 准备进度
    private LocalDateTime createdAt;     // 创建时间
}


package com.orbithy.sduonline_simulation.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("orders")
public class Order {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @TableId(type = IdType.AUTO)
    private Long id;                     // 主键
    @TableField("SDUId")
    private String SDUId;               // 外键，关联用户
    private String customerId;           // 客户ID
    private String customerName;         // 客户姓名
    private Integer price;            // 价格

    @JsonIgnore
    private String items;                // 数据库存储的 JSON 字符串

    private Integer total;               // 总数/总分
    private String status;               // 状态
    private LocalDateTime orderTime;     // 下单时间
    private Integer totalDevTime;        // 总开发时长
    private Integer preparationProgress; // 准备进度
    private LocalDateTime createdAt;     // 创建时间

    // 用于 JSON 序列化的 items 字段（返回给前端的是解析后的对象）
    @JsonProperty("items")
    public List<Map<String, Object>> getItemsAsObject() {
        if (items == null || items.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(items,
                objectMapper.getTypeFactory().constructCollectionType(List.class,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // 用于设置 items（接收 JSON 字符串）
    public void setItems(String itemsJson) {
        this.items = itemsJson;
    }

    // 用于设置 items（接收对象列表）
    public void setItems(List<Map<String, Object>> itemsList) {
        if (itemsList == null) {
            this.items = null;
            return;
        }
        try {
            this.items = objectMapper.writeValueAsString(itemsList);
        } catch (JsonProcessingException e) {
            this.items = null;
        }
    }
}


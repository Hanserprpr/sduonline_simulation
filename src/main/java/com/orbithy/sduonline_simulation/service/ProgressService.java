package com.orbithy.sduonline_simulation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbithy.sduonline_simulation.data.po.Order;
import com.orbithy.sduonline_simulation.data.po.User;
import com.orbithy.sduonline_simulation.data.vo.Result;
import com.orbithy.sduonline_simulation.mapper.OrderMapper;
import com.orbithy.sduonline_simulation.mapper.UserMapper;
import com.orbithy.sduonline_simulation.utils.LogUtil;
import com.orbithy.sduonline_simulation.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class ProgressService {

    private final LogUtil logUtil;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProgressService(LogUtil logUtil, UserMapper userMapper, OrderMapper orderMapper) {
        this.logUtil = logUtil;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public ResponseEntity<Result> begin(String SDUId, HttpServletRequest request) {
        try {
            String level = null;
            // 参数校验
            if (SDUId == null) {
                logUtil.error(null, null, request, level, "SDUId为空");
                return ResponseUtil.build(Result.error(400, "SDUId is required"));
            }

            User user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getSDUId, SDUId)
            );

            if (user == null) {
                logUtil.error(String.valueOf(SDUId), null, request, level, "用户不存在");
                return ResponseUtil.build(Result.error(404, "User not found"));
            }

            // 2) 随机选择客户（在三个客户中随机一人）
            String[][] customers = new String[][]{
                    {"C001", "阿里巴巴"},
                    {"C002", "腾讯"},
                    {"C003", "字节跳动"}
            };
            Random random = new Random();
            int ci = random.nextInt(customers.length);
            String customerId = customers[ci][0];
            String customerName = customers[ci][1];

            // 随机生成 items 数组（每个部分随机 id: 1,2,3 与 随机难度 1-7）
            // 3) 随机生成 items：产品、视觉、后端必有；前端和移动端两者至少一个


            // 定义元信息（任务类型 -> 基本信息）
            Map<String, Map<String, String>> itemMeta = new HashMap<>();
            {
                Map<String, String> meta = new HashMap<>();
                meta.put("id", "product_design");
                meta.put("name", "产品设计");
                meta.put("description", "定义需求和功能");
                itemMeta.put("product", meta);
            }
            {
                Map<String, String> meta = new HashMap<>();
                meta.put("id", "visual_design");
                meta.put("name", "视觉设计");
                meta.put("description", "设计UI和视觉稿");
                itemMeta.put("visual", meta);
            }
            {
                Map<String, String> meta = new HashMap<>();
                meta.put("id", "backend_dev");
                meta.put("name", "后端开发");
                meta.put("description", "开发服务器和数据库");
                itemMeta.put("backend", meta);
            }
            {
                Map<String, String> meta = new HashMap<>();
                meta.put("id", "frontend_dev");
                meta.put("name", "前端开发");
                meta.put("description", "实现用户界面");
                itemMeta.put("frontend", meta);
            }
            {
                Map<String, String> meta = new HashMap<>();
                meta.put("id", "mobile_dev");
                meta.put("name", "移动端开发");
                meta.put("description", "开发移动端应用");
                itemMeta.put("mobile", meta);
            }

            // 定义每个任务类型的7个小任务描述（这里以前端为用户指定的详细内容，其它用占位示例，可根据需要替换）
            Map<String, List<String>> subTasks = new HashMap<>();
            subTasks.put("frontend", List.of(
                    "开发纯静态网页，仅展示指定图文，确保Chrome打开无错乱，无需交互与适配。",
                    "开发带实时验证的表单页，支持结果反馈，兼容Chrome/Edge，可本地存储提交数据。",
                    "开发含视差动效的展示页，适配13英寸+屏幕，保证动效帧率≥30fps无卡顿。",
                    "开发多页面官网，实现无刷新跳转与数据传递，完成基础SEO关键词优化。",
                    "开发跨设备应用，Lighthouse得分≥90，兼容主流浏览器，适配10+主流设备机型。",
                    "开发电商管理后台，支持商品全流程管理、多维度数据可视化与细粒度权限控制。",
                    "主导开发千万级用户的前端架构，实现微服务拆分与跨端统一，保障高并发下0.5秒内响应，核心测试覆盖率≥90%，支持多团队协同与全球化多语言部署。"
            ));
            // 其它类型占位7项
            subTasks.put("product", List.of(
                    "撰写PRD文档并输出范围界定",
                    "制作需求原型并组织评审",
                    "梳理业务流程和系统边界",
                    "编写里程碑与验收标准",
                    "定义关键指标与追踪方案",
                    "组织跨部门沟通并确定排期",
                    "复盘需求并沉淀文档模板"
            ));
            subTasks.put("visual", List.of(
                    "输出风格版式与视觉规范",
                    "设计关键页面高保真稿",
                    "完成设计走查与标注",
                    "输出组件库与切图",
                    "适配暗黑模式",
                    "动效方案与微交互定义",
                    "设计验收与还原度评估"
            ));
            subTasks.put("backend", List.of(
                    "设计数据库与表结构",
                    "搭建基础认证与权限",
                    "实现RESTful接口与分页",
                    "接入缓存与限流措施",
                    "构建异步任务与消息队列",
                    "完善监控与日志追踪",
                    "压测与性能优化"
            ));
            subTasks.put("mobile", List.of(
                    "搭建移动端项目框架",
                    "适配主流分辨率与刘海屏",
                    "实现基础导航与路由",
                    "接入相机与存储等权限",
                    "优化首屏渲染与流畅度",
                    "完善离线缓存与数据同步",
                    "发布打包与多渠道分发"
            ));

            List<Map<String, Object>> items = new ArrayList<>();
            int totalDifficulty = 0;

            // 必选：产品、视觉、后端
            String[] required = {"product", "visual", "backend"};
            for (String type : required) {
                int difficulty = 1 + random.nextInt(3);
                Map<String, Object> item = new HashMap<>();
                Map<String, String> meta = new HashMap<>(itemMeta.get(type));
                // 从7个子任务中随机挑一个，写入描述字段，覆盖默认描述
                List<String> candidates = subTasks.get(type);
                if (candidates != null && !candidates.isEmpty()) {
                    meta.put("description", candidates.get(random.nextInt(candidates.size())));
                }
                item.put("item", meta);
                item.put("difficulty", difficulty);
                item.put("status", "pending");
                items.add(item);
                totalDifficulty += difficulty;
            }

            // 二选一：前端 或 移动端
            String optionalType = random.nextBoolean() ? "frontend" : "mobile";
            {
                int difficulty = 1 + random.nextInt(7);
                Map<String, Object> item = new HashMap<>();
                Map<String, String> meta = new HashMap<>(itemMeta.get(optionalType));
                List<String> candidates = subTasks.get(optionalType);
                if (candidates != null && !candidates.isEmpty()) {
                    meta.put("description", candidates.get(random.nextInt(candidates.size())));
                }
                item.put("item", meta);
                item.put("difficulty", difficulty);
                item.put("status", "pending");
                items.add(item);
                totalDifficulty += difficulty;
            }

            // 4) 价格 = 难度总和 * 200（其余逻辑保持不变）
            int price = totalDifficulty * 200;

            // 5) 组装 Order 持久化（其余逻辑保持不变）
            Order order = new Order();
            order.setSDUId(user.getSDUId());
            order.setCustomerId(customerId);
            order.setCustomerName(customerName);
            order.setPrice(price);
            order.setItems(objectMapper.writeValueAsString(items));
            order.setTotal(totalDifficulty);
            order.setStatus("preparing"); // 初始 preparing
            order.setOrderTime(LocalDateTime.now());
            order.setTotalDevTime(0);
            order.setPreparationProgress(0);
            order.setCreatedAt(LocalDateTime.now());

            orderMapper.insert(order);

            logUtil.info(String.valueOf(SDUId), null, request, level, "随机订单创建成功");
            return ResponseUtil.build(Result.success(order, "随机订单创建成功"));
        } catch (Exception e) {
            logUtil.error(String.valueOf(SDUId), null, request, null, "创建订单失败: " + e.getMessage());
            log.error("e: ", e);
            return ResponseUtil.build(Result.error(500, "Create order failed"));
        }
    }

    /**
     * 更改游戏状态接口
     * @param orderId 订单ID
     * @param items 任务项目JSON字符串（可选）
     * @param total 总分（可选）
     * @param status 状态（可选）
     * @param orderTime 订单时间（可选）
     * @param totalDevTime 总开发时长（可选）
     * @param preparationProgress 准备进度（可选）
     * @param request HTTP请求
     * @return 更新结果
     */
    @Transactional
    public ResponseEntity<Result> updateGameStatus(Long orderId, 
                                                  String items, 
                                                  Integer total, 
                                                  String status, 
                                                  LocalDateTime orderTime, 
                                                  Integer totalDevTime, 
                                                  Integer preparationProgress,
                                                  HttpServletRequest request) {
        try {
            // 参数校验
            if (orderId == null) {
                logUtil.error(null, null, request, "updateGameStatus", "订单ID为空");
                return ResponseUtil.build(Result.error(400, "Order ID is required"));
            }

            // 查找订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                logUtil.error(String.valueOf(orderId), null, request, "updateGameStatus", "订单不存在");
                return ResponseUtil.build(Result.error(404, "Order not found"));
            }

            // 更新字段（只更新非空的字段）
            boolean hasUpdates = false;
            boolean isFinishing = false; // 标记是否正在完成订单
            
            if (items != null && !items.trim().isEmpty()) {
                // 验证JSON格式
                try {
                    objectMapper.readTree(items);
                    order.setItems(items);
                    hasUpdates = true;
                } catch (Exception e) {
                    logUtil.error(String.valueOf(orderId), null, request, "updateGameStatus", "items格式错误: " + e.getMessage());
                    return ResponseUtil.build(Result.error(400, "Invalid items JSON format"));
                }
            }
            
            if (total != null) {
                order.setTotal(total);
                hasUpdates = true;
            }
            
            if (status != null && !status.trim().isEmpty()) {
                order.setStatus(status);
                hasUpdates = true;
                // 检查是否为完成状态
                if ("finish".equalsIgnoreCase(status)) {
                    isFinishing = true;
                }
            }
            
            if (orderTime != null) {
                order.setOrderTime(orderTime);
                hasUpdates = true;
            }
            
            if (totalDevTime != null) {
                order.setTotalDevTime(totalDevTime);
                hasUpdates = true;
            }
            
            if (preparationProgress != null) {
                order.setPreparationProgress(preparationProgress);
                hasUpdates = true;
            }

            if (!hasUpdates) {
                logUtil.error(String.valueOf(orderId), null, request, "updateGameStatus", "没有提供要更新的字段");
                return ResponseUtil.build(Result.error(400, "No fields to update"));
            }

            // 更新数据库
            int result = orderMapper.updateById(order);
            if (result > 0) {
                // 如果订单状态变为finish，则需要结算coins
                if (isFinishing) {
                    try {
                        User user = userMapper.selectOne(
                                new LambdaQueryWrapper<User>().eq(User::getSDUId, order.getSDUId())
                        );
                        if (user != null) {
                            // 获取订单价格作为奖励coins
                            if (total != null && total > 0) {
                                // 更新用户coins
                                Integer currentCoins = user.getCoins() != null ? user.getCoins() : 0;
                                Integer newCoins = currentCoins + total;
                                user.setCoins(newCoins);
                                
                                // 更新maxCoins（如果新的coins超过了历史最高）
                                int currentMaxCoins = user.getMaxCoins() != null ? user.getMaxCoins() : 0;
                                if (newCoins > currentMaxCoins) {
                                    user.setMaxCoins(newCoins);
                                }
                                
                                // 保存用户更新
                                int userUpdateResult = userMapper.updateById(user);
                                if (userUpdateResult > 0) {
                                    logUtil.info(String.valueOf(orderId), null, request, "updateGameStatus", 
                                        "游戏状态更新成功，用户coins增加: " + total + "，当前coins: " + newCoins);
                                } else {
                                    logUtil.error(String.valueOf(orderId), null, request, "updateGameStatus", "更新用户coins失败");
                                }
                            }
                        } else {
                            logUtil.error(String.valueOf(orderId), null, request, "updateGameStatus", "找不到对应用户，无法结算coins");
                        }
                    } catch (Exception e) {
                        logUtil.error(String.valueOf(orderId), null, request, "updateGameStatus", "结算coins失败: " + e.getMessage());
                        // 即使coins结算失败，订单状态更新也已成功，所以继续返回成功
                    }
                }
                
                logUtil.info(String.valueOf(orderId), null, request, "updateGameStatus", "游戏状态更新成功");
                return ResponseUtil.build(Result.success(order, "游戏状态更新成功"));
            } else {
                logUtil.error(String.valueOf(orderId), null, request, "updateGameStatus", "更新失败");
                return ResponseUtil.build(Result.error(500, "Update failed"));
            }

        } catch (Exception e) {
            logUtil.error(String.valueOf(orderId), null, request, "updateGameStatus", "更新游戏状态失败: " + e.getMessage());
            return ResponseUtil.build(Result.error(500, "Update game status failed"));
        }
    }

    /**
     * 结束游戏接口（暂时注释，如需要可启用）
     */
    @Transactional
    public ResponseEntity<Result> end(String sub, String level, HttpServletRequest request) {
        try {
            // 简单的结束逻辑，可根据需要扩展
            logUtil.info(sub, null, request, level, "游戏结束");
            return ResponseUtil.build(Result.success(null, "游戏结束"));
        } catch (Exception e) {
            logUtil.error(sub, null, request, level, "结束游戏失败: " + e.getMessage());
            return ResponseUtil.build(Result.error(500, "End game failed"));
        }
    }
}
